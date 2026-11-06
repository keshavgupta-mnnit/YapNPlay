package com.kglabs28.yapnplay.appengine.games.cyberblast.ui.components

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import com.kglabs28.yapnplay.appengine.games.cyberblast.BlastDir
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sin

/** How a player's character looks this moment. */
data class CyberRunnerLook(
    val suit: Color,
    val facing: BlastDir,
    val moving: Boolean,
    val shielded: Boolean,
    val boosted: Boolean,
)

/**
 * A player's character, standing on its tile's centre ([xTiles], [yTiles]):
 * a small cyber runner in a round helmet with a dark visor and glowing eyes,
 * its helmet and suit in the player's colour, white trims and a belt. It
 * bobs and swings its legs while walking, blinks while shielded, and trails
 * speed lines with the boots on.
 */
fun DrawScope.drawRunner(grid: CyberGrid, xTiles: Float, yTiles: Float, look: CyberRunnerLook, timeMs: Long, p: CyberBoardPalette) {
    if (look.shielded && (timeMs / BLINK_MS) % 2 == 0L) return
    val t = grid.tile
    // The character is drawn a little bigger than its tile's share, so it reads clearly at phone size.
    val u = t * SCALE
    val cx = grid.x(xTiles)
    val step = if (look.moving) sin(timeMs * STEP_SPEED * PI / MILLIS).toFloat() else 0f
    val feet = grid.y(yTiles) + t * FEET
    val cy = feet - u * 0.36f - u * BOB * abs(step)
    val dark = lerp(look.suit, Color.Black, SHADE)
    val light = lerp(look.suit, Color.White, LIGHTEN)
    val line = u * OUTLINE
    drawOval(Color.Black.copy(alpha = SHADOW_ALPHA), Offset(cx - u * 0.26f, feet - u * 0.05f), Size(u * 0.52f, u * 0.12f))
    if (look.boosted && look.moving) speedLines(cx, cy, u, look.facing, p)
    val swing = step * u * LEG_SWING
    val head = Offset(cx, cy - u * 0.14f)
    val headR = u * 0.26f
    // Every part twice: first a dark outline a little larger, then its colour.
    fun legs(grow: Float, colour: Color) {
        val w = u * 0.13f + grow * 2
        val h = u * 0.15f + grow * 2
        drawRoundRect(colour, Offset(cx - u * 0.16f - grow, cy + u * 0.2f + swing - grow), Size(w, h), CornerRadius(w / 2))
        drawRoundRect(colour, Offset(cx + u * 0.03f - grow, cy + u * 0.2f - swing - grow), Size(w, h), CornerRadius(w / 2))
    }
    fun body(grow: Float, colour: Color) = drawRoundRect(colour, Offset(cx - u * 0.2f - grow, cy + u * 0.02f - grow), Size(u * 0.4f + grow * 2, u * 0.24f + grow * 2), CornerRadius(u * 0.1f + grow))
    fun hands(grow: Float, colour: Color) {
        drawCircle(colour, u * 0.07f + grow, Offset(cx - u * 0.26f, cy + u * 0.13f - swing))
        drawCircle(colour, u * 0.07f + grow, Offset(cx + u * 0.26f, cy + u * 0.13f + swing))
    }
    fun helmet(grow: Float, colour: Color, pads: Color) {
        drawCircle(pads, u * 0.075f + grow, Offset(head.x - headR, head.y + u * 0.03f))
        drawCircle(pads, u * 0.075f + grow, Offset(head.x + headR, head.y + u * 0.03f))
        drawCircle(colour, headR + grow, head)
    }
    legs(line, p.outline)
    body(line, p.outline)
    hands(line, p.outline)
    helmet(line, p.outline, p.outline)
    legs(0f, dark)
    body(0f, look.suit)
    drawRect(p.trim, Offset(cx - u * 0.2f, cy + u * 0.15f), Size(u * 0.4f, u * 0.045f))
    hands(0f, p.trim)
    helmet(0f, look.suit, dark)
    // A light crest stripe and a shine on the helmet.
    drawRoundRect(light, Offset(head.x - u * 0.035f, head.y - headR), Size(u * 0.07f, u * 0.15f), CornerRadius(u * 0.035f))
    drawOval(Color.White.copy(alpha = HELMET_SHINE), Offset(head.x - headR * 0.75f, head.y - headR * 0.75f), Size(headR * 0.5f, headR * 0.32f))
    visor(head, headR, u, look.facing, p)
    if (look.shielded) drawCircle(p.eye.copy(alpha = SHIELD_ALPHA), u * 0.5f, Offset(cx, cy), style = Stroke(u * 0.04f))
}

/** The visor turns with the player; from behind only its rim shows. */
private fun DrawScope.visor(head: Offset, headR: Float, t: Float, facing: BlastDir, p: CyberBoardPalette) {
    if (facing == BlastDir.UP) {
        drawRoundRect(p.visor.copy(alpha = BACK_VISOR_ALPHA), Offset(head.x - headR * 0.7f, head.y + headR * 0.35f), Size(headR * 1.4f, headR * 0.2f), CornerRadius(headR * 0.1f))
        return
    }
    val shift = when (facing) {
        BlastDir.LEFT -> -headR * 0.3f
        BlastDir.RIGHT -> headR * 0.3f
        else -> 0f
    }
    val w = if (facing == BlastDir.DOWN) headR * 1.6f else headR * 1.3f
    val h = headR * 0.85f
    val left = head.x - w / 2 + shift
    val top = head.y - h / 2 + headR * 0.1f
    drawRoundRect(p.visor, Offset(left, top), Size(w, h), CornerRadius(h / 2))
    val eyeR = t * 0.055f
    val eyeY = top + h / 2
    if (facing == BlastDir.DOWN) {
        drawCircle(p.eye, eyeR, Offset(head.x - w * 0.22f, eyeY))
        drawCircle(p.eye, eyeR, Offset(head.x + w * 0.22f, eyeY))
    } else {
        val dir = if (facing == BlastDir.LEFT) -1 else 1
        drawCircle(p.eye, eyeR, Offset(head.x + shift + dir * w * 0.2f, eyeY))
        drawCircle(p.eye, eyeR * 0.8f, Offset(head.x + shift - dir * w * 0.1f, eyeY))
    }
    drawLine(Color.White.copy(alpha = VISOR_GLINT), Offset(left + w * 0.2f, top + h * 0.25f), Offset(left + w * 0.45f, top + h * 0.25f), t * 0.02f)
}

/** Lines streaming out behind a boosted player. */
private fun DrawScope.speedLines(cx: Float, cy: Float, t: Float, facing: BlastDir, p: CyberBoardPalette) {
    val back = Offset(-facing.dx.toFloat(), -facing.dy.toFloat())
    val side = Offset(back.y, back.x)
    listOf(-0.15f, 0f, 0.15f).forEach { o ->
        val from = Offset(cx + back.x * t * 0.3f + side.x * t * o, cy + back.y * t * 0.3f + side.y * t * o)
        val to = Offset(from.x + back.x * t * 0.3f, from.y + back.y * t * 0.3f)
        drawLine(p.bootWing.copy(alpha = SPEED_ALPHA), from, to, t * 0.03f)
    }
}

private const val MILLIS = 1_000.0
private const val STEP_SPEED = 9.0
private const val BOB = 0.05f
private const val LEG_SWING = 0.04f
private const val SHADE = 0.35f
private const val LIGHTEN = 0.45f
private const val SHADOW_ALPHA = 0.35f
private const val BLINK_MS = 110L
private const val SHIELD_ALPHA = 0.6f
private const val BACK_VISOR_ALPHA = 0.6f
private const val VISOR_GLINT = 0.5f
private const val SPEED_ALPHA = 0.7f
private const val SCALE = 1.25f
private const val OUTLINE = 0.035f
private const val FEET = 0.42f
private const val HELMET_SHINE = 0.35f

/** Under the user's own character: a pulsing glow and ring in their colour, so they always spot themselves. */
fun DrawScope.drawYouGlow(grid: CyberGrid, xTiles: Float, yTiles: Float, timeMs: Long, colour: Color, p: CyberBoardPalette) {
    val t = grid.tile
    val c = Offset(grid.x(xTiles), grid.y(yTiles) + t * YOU_DROP)
    val pulse = 0.5f + 0.5f * sin(timeMs * YOU_PULSE * PI / MILLIS).toFloat()
    val r = t * (YOU_GLOW + YOU_GLOW_PULSE * pulse)
    drawCircle(androidx.compose.ui.graphics.Brush.radialGradient(listOf(colour.copy(alpha = YOU_GLOW_ALPHA), colour.copy(alpha = 0f)), c, r), r, c)
    drawOval(p.trim.copy(alpha = YOU_RING_ALPHA * (0.6f + 0.4f * pulse)), Offset(c.x - t * 0.36f, c.y + t * 0.18f), Size(t * 0.72f, t * 0.22f), style = Stroke(t * 0.04f))
}

/** Over the user's own character: a small bobbing arrow in their colour pointing down at them. */
fun DrawScope.drawYouMarker(grid: CyberGrid, xTiles: Float, yTiles: Float, timeMs: Long, colour: Color, p: CyberBoardPalette) {
    val t = grid.tile
    val bob = t * YOU_BOB * sin(timeMs * YOU_PULSE * PI / MILLIS).toFloat()
    val tipY = grid.y(yTiles) - t * YOU_MARK_UP + bob
    val cx = grid.x(xTiles)
    val arrow = androidx.compose.ui.graphics.Path().apply {
        moveTo(cx, tipY)
        lineTo(cx - t * 0.12f, tipY - t * 0.16f)
        lineTo(cx + t * 0.12f, tipY - t * 0.16f)
        close()
    }
    drawPath(arrow, p.outline, style = Stroke(t * 0.05f))
    drawPath(arrow, colour)
    drawPath(arrow, p.trim, style = Stroke(t * 0.02f))
}

private const val YOU_DROP = 0.1f
private const val YOU_PULSE = 2.4
private const val YOU_GLOW = 0.6f
private const val YOU_GLOW_PULSE = 0.12f
private const val YOU_GLOW_ALPHA = 0.55f
private const val YOU_RING_ALPHA = 0.9f
private const val YOU_BOB = 0.05f
private const val YOU_MARK_UP = 0.62f
