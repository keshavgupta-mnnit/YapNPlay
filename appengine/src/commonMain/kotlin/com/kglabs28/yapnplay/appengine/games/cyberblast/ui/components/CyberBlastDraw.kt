package com.kglabs28.yapnplay.appengine.games.cyberblast.ui.components

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.drawText
import com.kglabs28.yapnplay.appengine.games.cyberblast.FlameShape
import kotlin.math.PI
import kotlin.math.sin

/**
 * A bomb in its owner's [glow]: a dark ball with a shine, a burning fuse,
 * swelling faster as [fuseLeft] (0–1 of the fuse) runs down.
 */
fun DrawScope.drawBomb(grid: CyberGrid, col: Int, row: Int, fuseLeft: Float, timeMs: Long, glow: Color, p: CyberBoardPalette) {
    val t = grid.tile
    val urgency = 1 - fuseLeft
    val beat = sin(timeMs * (PULSE_SLOW + PULSE_FAST * urgency) * PI / MILLIS).toFloat()
    val r = t * (BOMB_RADIUS + BOMB_SWELL * (0.5f + 0.5f * beat) * (0.3f + urgency))
    val c = Offset(grid.x(col + 0.5f), grid.y(row + 0.55f))
    drawCircle(Brush.radialGradient(listOf(glow.copy(alpha = GLOW_ALPHA), Color.Transparent), c, r * GLOW_SCALE), r * GLOW_SCALE, c)
    drawCircle(p.bombBody, r, c)
    drawCircle(p.bombShine.copy(alpha = SHINE_ALPHA), r * SHINE_SIZE, Offset(c.x - r * SHINE_OFF, c.y - r * SHINE_OFF))
    drawCircle(glow, r, c, style = Stroke(t * RIM))
    val fuseTop = Offset(c.x + r * FUSE_X, c.y - r * FUSE_Y)
    drawLine(p.bombShine, Offset(c.x + r * CAP_X, c.y - r * CAP_Y), fuseTop, t * FUSE_WIDTH, StrokeCap.Round)
    val flicker = 0.6f + 0.4f * sin(timeMs * SPARK_SPEED * PI / MILLIS).toFloat()
    drawCircle(p.spark.copy(alpha = flicker), t * SPARK * (0.8f + 0.4f * flicker), fuseTop)
    drawCircle(p.fireCore, t * SPARK * HOT_CORE, fuseTop)
}

/**
 * Fire on a tile in its owner's [colour]: a white-hot core fading through
 * the colour, along the arm ([shape]) or round the centre; it flickers and
 * thins as [left] (0–1) runs out.
 */
fun DrawScope.drawFlame(grid: CyberGrid, col: Int, row: Int, shape: FlameShape, left: Float, timeMs: Long, colour: Color, p: CyberBoardPalette) {
    val t = grid.tile
    val x = grid.x(col.toFloat())
    val y = grid.y(row.toFloat())
    val size = 0.8f + 0.2f * left
    val bright = lerp(colour, p.fireCore, BRIGHTEN)
    // A band of the player's colour joins the puffs, so the arm reads as one stream of fire.
    val band = t * ARM * size
    val bandColours = listOf(Color.Transparent, colour.copy(alpha = OUTER_ALPHA), bright, colour.copy(alpha = OUTER_ALPHA), Color.Transparent)
    when (shape) {
        FlameShape.HORIZONTAL -> drawRect(Brush.verticalGradient(bandColours, y + t / 2 - band / 2, y + t / 2 + band / 2), Offset(x, y + t / 2 - band / 2), Size(t, band))
        FlameShape.VERTICAL -> drawRect(Brush.horizontalGradient(bandColours, x + t / 2 - band / 2, x + t / 2 + band / 2), Offset(x + t / 2 - band / 2, y), Size(band, t))
        FlameShape.CENTRE -> Unit
    }
    // Flickering puffs: white-hot in the middle, the player's colour round them, fading out.
    val spots = when (shape) {
        FlameShape.HORIZONTAL -> PUFF_SPOTS.map { Offset(x + t * it, y + t / 2) }
        FlameShape.VERTICAL -> PUFF_SPOTS.map { Offset(x + t / 2, y + t * it) }
        FlameShape.CENTRE -> listOf(Offset(x + t / 2, y + t / 2))
    }
    val base = if (shape == FlameShape.CENTRE) CENTRE else PUFF
    spots.forEachIndexed { i, c ->
        val wobble = 1f + FLICKER * sin((timeMs + (col * PHASE_STEP + row * PHASE_STEP * 2 + i * PHASE_STEP)) * FLICKER_SPEED * PI / MILLIS).toFloat()
        val r = t * base * size * wobble
        drawCircle(Brush.radialGradient(listOf(p.fireCore, bright, colour, colour.copy(alpha = OUTER_ALPHA), Color.Transparent), c, r), r, c)
    }
}

/** A bomb's flash as it goes off ([age] 0–1): a ring in the owner's colour opening up. */
fun DrawScope.drawBoomRing(grid: CyberGrid, col: Int, row: Int, age: Float, colour: Color) {
    val t = grid.tile
    val c = Offset(grid.x(col + 0.5f), grid.y(row + 0.5f))
    drawCircle(colour.copy(alpha = (1 - age) * RING_ALPHA), t * (RING_FROM + RING_GROW * age), c, style = Stroke(t * RING_WIDTH * (1 - age)))
}

/** Speed boots on the floor: a winged boot with "+30s", blinking when nearly gone. */
fun DrawScope.drawBoots(grid: CyberGrid, col: Int, row: Int, msLeft: Long, timeMs: Long, p: CyberBoardPalette, text: CyberBoardText, tag: String) {
    if (msLeft < BLINK_MS && (timeMs / BLINK_STEP_MS) % 2 == 0L) return
    val t = grid.tile
    val x = grid.x(col.toFloat())
    val y = grid.y(row.toFloat()) - t * BOB * sin(timeMs * BOB_SPEED * PI / MILLIS).toFloat()
    val c = Offset(x + t / 2, y + t * 0.55f)
    drawCircle(Brush.radialGradient(listOf(p.bootWing.copy(alpha = GLOW_ALPHA), Color.Transparent), c, t * 0.55f), t * 0.55f, c)
    // The boot: a shaft and a foot with a pale sole.
    drawRoundRect(p.boot, Offset(x + t * 0.36f, y + t * 0.28f), Size(t * 0.2f, t * 0.32f), CornerRadius(t * 0.05f))
    drawRoundRect(p.boot, Offset(x + t * 0.36f, y + t * 0.5f), Size(t * 0.36f, t * 0.16f), CornerRadius(t * 0.07f))
    drawRect(p.trim, Offset(x + t * 0.36f, y + t * 0.64f), Size(t * 0.36f, t * 0.04f))
    val wing = Path().apply {
        moveTo(x + t * 0.36f, y + t * 0.34f)
        lineTo(x + t * 0.16f, y + t * 0.26f)
        lineTo(x + t * 0.22f, y + t * 0.38f)
        lineTo(x + t * 0.14f, y + t * 0.42f)
        lineTo(x + t * 0.36f, y + t * 0.46f)
        close()
    }
    drawPath(wing, p.bootWing)
    val layout = text.measurer.measure(tag, text.style.copy(color = p.bootWing))
    drawText(layout, topLeft = Offset(x + t / 2 - layout.size.width / 2f, y + t * 0.7f))
}

/** Points floating up over a player ([age] 0–1): green for a gain, red for a loss. */
fun DrawScope.drawPop(grid: CyberGrid, xTiles: Float, yTiles: Float, label: String, age: Float, colour: Color, text: CyberBoardText) {
    val layout = text.measurer.measure(label, text.style.copy(color = colour.copy(alpha = 1 - age)))
    val x = grid.x(xTiles) - layout.size.width / 2f
    val y = grid.y(yTiles) - grid.tile * (POP_FROM + POP_RISE * age) - layout.size.height
    drawText(layout, topLeft = Offset(x, y))
}

private const val MILLIS = 1_000.0
private const val PULSE_SLOW = 2.0
private const val PULSE_FAST = 8.0
private const val BOMB_RADIUS = 0.34f
private const val BOMB_SWELL = 0.05f
private const val GLOW_SCALE = 1.6f
private const val GLOW_ALPHA = 0.7f
private const val SHINE_ALPHA = 0.7f
private const val SHINE_SIZE = 0.28f
private const val SHINE_OFF = 0.35f
private const val RIM = 0.06f
private const val FUSE_X = 0.55f
private const val FUSE_Y = 1.2f
private const val CAP_X = 0.35f
private const val CAP_Y = 0.85f
private const val FUSE_WIDTH = 0.05f
private const val SPARK = 0.08f
private const val HOT_CORE = 0.45f
private const val SPARK_SPEED = 14.0
private const val ARM = 0.78f
private const val PUFF = 0.62f
private val PUFF_SPOTS = listOf(0.25f, 0.75f)
private const val BRIGHTEN = 0.45f
private const val CENTRE = 0.85f
private const val FLICKER = 0.12f
private const val FLICKER_SPEED = 18.0
private const val PHASE_STEP = 37
private const val OUTER_ALPHA = 0.7f
private const val RING_ALPHA = 0.9f
private const val RING_FROM = 0.3f
private const val RING_GROW = 0.9f
private const val RING_WIDTH = 0.12f
private const val BLINK_MS = 3_000L
private const val BLINK_STEP_MS = 150L
private const val BOB = 0.04f
private const val BOB_SPEED = 2.0
private const val POP_FROM = 0.4f
private const val POP_RISE = 0.8f
