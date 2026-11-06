package com.kglabs28.yapnplay.appengine.games.cyberblast.ui.components

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.lerp
import com.kglabs28.yapnplay.appengine.games.cyberblast.FlameShape
import com.kglabs28.yapnplay.appengine.games.cyberblast.utils.CyberFlameTips

/**
 * Fire on one tile in its owner's [colour], drawn soft like real flames: a
 * wide glow, then overlapping flame puffs (white-hot in the middle, a warm
 * glow, the player's colour, fading to nothing at their edges) that flicker
 * and change size every few frames, and a bright streak along the arm. It
 * tapers towards an arm's end ([tipSide] −1 / +1); a blast's centre is one big
 * flaring puff. It fades a little as [left] (0–1) runs out, and throws sparks.
 */
fun DrawScope.drawFire(grid: CyberGrid, tile: Int, col: Int, row: Int, shape: FlameShape, tipSide: Int, left: Float, timeMs: Long, colour: Color, p: CyberBoardPalette) {
    val t = grid.tile
    val cx = grid.x(col + 0.5f)
    val cy = grid.y(row + 0.5f)
    val size = SHRINK_MIN + (1 - SHRINK_MIN) * left
    val warm = lerp(colour, p.fireWarm, WARM_MIX)
    val puffColours = listOf(p.fireCore, p.fireCore, warm, colour, colour.copy(alpha = EDGE_ALPHA), Color.Transparent)
    val step = timeMs / FLICKER_MS
    if (shape == FlameShape.CENTRE) {
        val flare = 1f + FLARE * (CyberFlameTips.noise(tile, step, 0) - 0.5f)
        val r = t * CENTRE * size * flare
        drawCircle(Brush.radialGradient(listOf(colour.copy(alpha = GLOW_ALPHA), Color.Transparent), Offset(cx, cy), r * GLOW_SCALE), r * GLOW_SCALE, Offset(cx, cy))
        drawCircle(Brush.radialGradient(puffColours, Offset(cx, cy), r), r, Offset(cx, cy))
        sparks(grid, tile, col, row, timeMs, size, warm, p)
        return
    }
    val horizontal = shape == FlameShape.HORIZONTAL
    // How thick the fire is at a point along the tile (0–1): full, narrowing towards an arm's end.
    fun taper(s: Float): Float = when (tipSide) {
        1 -> 1f - TAPER * s
        -1 -> 1f - TAPER * (1 - s)
        else -> 1f
    }
    fun along(s: Float, across: Float): Offset = if (horizontal) Offset(cx + (s - 0.5f) * t, cy + across) else Offset(cx + across, cy + (s - 0.5f) * t)
    // A wide soft glow under the whole arm.
    val glowHalf = t * ARM * GLOW_SCALE * size
    if (horizontal) {
        drawRect(Brush.verticalGradient(listOf(Color.Transparent, colour.copy(alpha = GLOW_ALPHA), Color.Transparent), cy - glowHalf, cy + glowHalf), Offset(cx - t / 2, cy - glowHalf), Size(t, glowHalf * 2))
    } else {
        drawRect(Brush.horizontalGradient(listOf(Color.Transparent, colour.copy(alpha = GLOW_ALPHA), Color.Transparent), cx - glowHalf, cx + glowHalf), Offset(cx - glowHalf, cy - t / 2), Size(glowHalf * 2, t))
    }
    // Overlapping puffs that flicker in size and wobble across the arm.
    PUFFS.forEachIndexed { k, s ->
        val n = CyberFlameTips.noise(tile, step, k + 1)
        val r = t * ARM * size * taper(s) * (PUFF_MIN + PUFF_RANGE * n)
        val wobble = t * WOBBLE * (CyberFlameTips.noise(tile, step, k + PUFFS.size + 1) - 0.5f)
        val c = along(s, wobble)
        drawCircle(Brush.radialGradient(puffColours, c, r), r, c)
    }
    // The white-hot streak through the middle.
    val from = along(0f, 0f)
    val to = along(1f, 0f)
    val core = t * CORE * size
    val start = if (tipSide == -1) along(TIP_START, 0f) else from
    val end = if (tipSide == 1) along(1 - TIP_START, 0f) else to
    drawLine(p.fireCore.copy(alpha = CORE_ALPHA), start, end, core, StrokeCap.Round)
    sparks(grid, tile, col, row, timeMs, size, warm, p)
}

/** A few sparks jumping round the fire, new places every few frames. */
private fun DrawScope.sparks(grid: CyberGrid, tile: Int, col: Int, row: Int, timeMs: Long, size: Float, warm: Color, p: CyberBoardPalette) {
    val t = grid.tile
    val step = timeMs / SPARK_STEP_MS
    repeat(SPARKS) { k ->
        val at = Offset(grid.x(col + CyberFlameTips.noise(tile, step, SPARK_SALT + k * 2)), grid.y(row + CyberFlameTips.noise(tile, step, SPARK_SALT + k * 2 + 1)))
        val r = t * SPARK_RADIUS * size
        drawCircle(Brush.radialGradient(listOf(p.fireCore, warm, Color.Transparent), at, r * 2), r * 2, at)
    }
}

/** Where the puffs sit along a tile (they overlap the next tile's, so the arm reads as one fire). */
private val PUFFS = listOf(0.1f, 0.42f, 0.74f, 1.0f)
private const val ARM = 0.5f
private const val CENTRE = 0.75f
private const val PUFF_MIN = 0.85f
private const val PUFF_RANGE = 0.35f
private const val WOBBLE = 0.12f
private const val FLARE = 0.25f
private const val CORE = 0.16f
private const val CORE_ALPHA = 0.85f
private const val TIP_START = 0.45f
private const val GLOW_SCALE = 1.6f
private const val GLOW_ALPHA = 0.35f
private const val EDGE_ALPHA = 0.45f
private const val WARM_MIX = 0.7f
private const val SHRINK_MIN = 0.75f
private const val TAPER = 0.7f
private const val FLICKER_MS = 70L
private const val SPARKS = 2
private const val SPARK_SALT = 40
private const val SPARK_STEP_MS = 90L
private const val SPARK_RADIUS = 0.045f
