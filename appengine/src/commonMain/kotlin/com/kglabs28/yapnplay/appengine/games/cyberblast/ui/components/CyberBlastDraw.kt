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
 * A bomb in its owner's [glow] colour: a glossy glass sphere, deep at its rim, the
 * player's colour through it with a second, cooler sheen on the lower right and a
 * bright highlight, a metal cap, a curled fuse with a sparkling star; it swells
 * faster as [fuseLeft] (0–1 of the fuse) runs down.
 */
fun DrawScope.drawBomb(grid: CyberGrid, col: Int, row: Int, fuseLeft: Float, timeMs: Long, glow: Color, p: CyberBoardPalette) {
    val t = grid.tile
    val urgency = 1 - fuseLeft
    val beat = sin(timeMs * (PULSE_SLOW + PULSE_FAST * urgency) * PI / MILLIS).toFloat()
    val r = t * (BOMB_RADIUS + BOMB_SWELL * (0.5f + 0.5f * beat) * (0.3f + urgency))
    val c = Offset(grid.x(col + 0.5f), grid.y(row + 0.56f))
    drawCircle(Brush.radialGradient(listOf(glow.copy(alpha = GLOW_ALPHA), Color.Transparent), c, r * GLOW_SCALE), r * GLOW_SCALE, c)
    drawOval(Color.Black.copy(alpha = 0.35f), Offset(c.x - r * 0.8f, c.y + r * 0.7f), Size(r * 1.6f, r * 0.45f))
    drawBombSphere(c, r, glow, timeMs, p)
}

/** The glass bomb itself, centred on [c] with radius [r] (the board's bombs and the Drop Bomb button). */
fun DrawScope.drawBombSphere(c: Offset, r: Float, colour: Color, timeMs: Long, p: CyberBoardPalette) {
    val deep = lerp(colour, Color.Black, DEEP)
    val light = lerp(colour, Color.White, LIGHT)
    val cool = lerp(colour, p.bombTeal, COOL)
    drawCircle(Brush.radialGradient(listOf(light, colour, deep), Offset(c.x - r * 0.3f, c.y - r * 0.35f), r * 1.3f), r, c)
    drawCircle(Brush.radialGradient(listOf(cool.copy(alpha = 0.8f), Color.Transparent), Offset(c.x + r * 0.45f, c.y + r * 0.45f), r * 0.75f), r, c)
    drawCircle(light.copy(alpha = 0.6f), r, c, style = Stroke(r * 0.06f))
    drawOval(Color.White.copy(alpha = SHINE_ALPHA), Offset(c.x - r * 0.62f, c.y - r * 0.62f), Size(r * 0.55f, r * 0.34f))
    drawCircle(Color.White.copy(alpha = SHINE_ALPHA * 0.6f), r * 0.08f, Offset(c.x + r * 0.35f, c.y + r * 0.5f))
    drawRoundRect(p.bombCap, Offset(c.x + r * 0.15f, c.y - r * 1.08f), Size(r * 0.42f, r * 0.3f), CornerRadius(r * 0.08f))
    val fuse = Path().apply {
        moveTo(c.x + r * 0.36f, c.y - r * 1.06f)
        quadraticTo(c.x + r * 0.5f, c.y - r * 1.5f, c.x + r * 0.85f, c.y - r * 1.4f)
    }
    drawPath(fuse, p.bombShine, style = Stroke(r * FUSE_OF_RADIUS, cap = StrokeCap.Round))
    val tip = Offset(c.x + r * 0.85f, c.y - r * 1.4f)
    val flicker = 0.6f + 0.4f * sin(timeMs * SPARK_SPEED * PI / MILLIS).toFloat()
    val spark = r * SPARK_OF_RADIUS
    drawCircle(Brush.radialGradient(listOf(p.spark, Color.Transparent), tip, spark * 2.5f), spark * 2.5f * flicker, tip)
    val ray = spark * 1.8f * flicker
    drawLine(p.fireCore, Offset(tip.x - ray, tip.y), Offset(tip.x + ray, tip.y), r * 0.07f, StrokeCap.Round)
    drawLine(p.fireCore, Offset(tip.x, tip.y - ray), Offset(tip.x, tip.y + ray), r * 0.07f, StrokeCap.Round)
    drawCircle(p.fireCore, spark * HOT_CORE, tip)
}

/** A bomb's flash as it goes off ([age] 0–1): a ring in the owner's colour opening up. */
fun DrawScope.drawBoomRing(grid: CyberGrid, col: Int, row: Int, age: Float, colour: Color) {
    val t = grid.tile
    val c = Offset(grid.x(col + 0.5f), grid.y(row + 0.5f))
    drawCircle(colour.copy(alpha = (1 - age) * RING_ALPHA), t * (RING_FROM + RING_GROW * age), c, style = Stroke(t * RING_WIDTH * (1 - age)))
}

/** Speed boots on the floor: an orange winged sneaker with a white sole and laces, "+30s" under it, blinking when nearly gone. */
fun DrawScope.drawBoots(grid: CyberGrid, col: Int, row: Int, msLeft: Long, timeMs: Long, p: CyberBoardPalette, text: CyberBoardText, tag: String) {
    if (msLeft < BLINK_MS && (timeMs / BLINK_STEP_MS) % 2 == 0L) return
    val t = grid.tile
    val x = grid.x(col.toFloat())
    val y = grid.y(row.toFloat()) - t * BOB * sin(timeMs * BOB_SPEED * PI / MILLIS).toFloat()
    fun at(px: Float, py: Float) = Offset(x + t * px, y + t * py)
    val c = at(0.52f, 0.5f)
    drawCircle(Brush.radialGradient(listOf(p.bootWing.copy(alpha = GLOW_ALPHA), Color.Transparent), c, t * 0.6f), t * 0.6f, c)
    // The wing behind the heel: three cyan feathers.
    listOf(0.3f to 0.36f, 0.27f to 0.44f, 0.26f to 0.52f).forEachIndexed { i, (fx, fy) ->
        drawLine(p.bootWing, at(fx, fy), at(fx - 0.17f + i * 0.02f, fy - 0.08f + i * 0.02f), t * 0.06f, StrokeCap.Round)
    }
    // The shoe's upper: heel, collar, tongue, then the toe sloping down.
    val upper = Path().apply {
        moveTo(x + t * 0.26f, y + t * 0.66f)
        lineTo(x + t * 0.28f, y + t * 0.36f)
        quadraticTo(x + t * 0.38f, y + t * 0.3f, x + t * 0.46f, y + t * 0.36f)
        lineTo(x + t * 0.54f, y + t * 0.44f)
        quadraticTo(x + t * 0.8f, y + t * 0.48f, x + t * 0.86f, y + t * 0.62f)
        lineTo(x + t * 0.86f, y + t * 0.66f)
        close()
    }
    drawPath(upper, Brush.verticalGradient(listOf(p.bootLight, p.boot), y + t * 0.3f, y + t * 0.66f))
    drawPath(upper, p.bootEdge, style = Stroke(t * 0.025f))
    // The white swoosh and laces.
    drawLine(p.trim, at(0.4f, 0.58f), at(0.72f, 0.54f), t * 0.035f, StrokeCap.Round)
    listOf(0.48f, 0.55f, 0.62f).forEach { lx -> drawLine(p.trim, at(lx, 0.43f), at(lx + 0.04f, 0.49f), t * 0.025f, StrokeCap.Round) }
    // The sole.
    drawRoundRect(p.trim, at(0.22f, 0.65f), Size(t * 0.68f, t * 0.09f), CornerRadius(t * 0.045f))
    drawRoundRect(p.bootEdge, at(0.22f, 0.65f), Size(t * 0.68f, t * 0.09f), CornerRadius(t * 0.045f), style = Stroke(t * 0.02f))
    val layout = text.measurer.measure(tag, text.style.copy(color = p.bootWing))
    drawText(layout, topLeft = Offset(x + t / 2 - layout.size.width / 2f, y + t * 0.76f))
}

/** Points floating up over a player ([age] 0–1): green for a gain, red for a loss. */
fun DrawScope.drawPop(grid: CyberGrid, xTiles: Float, yTiles: Float, label: String, age: Float, colour: Color, text: CyberBoardText) {
    val layout = text.measurer.measure(label, text.style.copy(color = colour.copy(alpha = 1 - age)))
    val x = grid.x(xTiles) - layout.size.width / 2f
    val y = grid.y(yTiles) - grid.tile * (POP_FROM + POP_RISE * age) - layout.size.height
    drawText(layout, topLeft = Offset(x, y))
}

private const val MILLIS = 1_000.0
private const val DEEP = 0.65f
private const val LIGHT = 0.55f
private const val COOL = 0.5f
private const val FUSE_OF_RADIUS = 0.15f
private const val SPARK_OF_RADIUS = 0.16f
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
