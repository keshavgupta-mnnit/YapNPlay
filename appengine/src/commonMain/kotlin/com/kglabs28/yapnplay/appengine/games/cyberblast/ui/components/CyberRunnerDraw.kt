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
    val cx = grid.x(xTiles)
    val step = if (look.moving) sin(timeMs * STEP_SPEED * PI / MILLIS).toFloat() else 0f
    val cy = grid.y(yTiles) - t * BOB * abs(step)
    val dark = lerp(look.suit, Color.Black, SHADE)
    val light = lerp(look.suit, Color.White, LIGHTEN)
    // Shadow on the floor.
    drawOval(Color.Black.copy(alpha = SHADOW_ALPHA), Offset(cx - t * 0.26f, grid.y(yTiles) + t * 0.3f), Size(t * 0.52f, t * 0.14f))
    if (look.boosted && look.moving) speedLines(cx, cy, t, look.facing, p)
    // Legs, swinging.
    val legW = t * 0.12f
    val legH = t * 0.16f
    drawRoundRect(dark, Offset(cx - t * 0.15f, cy + t * 0.2f + step * t * LEG_SWING), Size(legW, legH), CornerRadius(legW / 2))
    drawRoundRect(dark, Offset(cx + t * 0.03f, cy + t * 0.2f - step * t * LEG_SWING), Size(legW, legH), CornerRadius(legW / 2))
    // Body with a white belt.
    drawRoundRect(look.suit, Offset(cx - t * 0.2f, cy + t * 0.02f), Size(t * 0.4f, t * 0.24f), CornerRadius(t * 0.1f))
    drawRect(p.trim, Offset(cx - t * 0.2f, cy + t * 0.15f), Size(t * 0.4f, t * 0.04f))
    // Arms (round gloves), swinging against the legs.
    drawCircle(p.trim, t * 0.065f, Offset(cx - t * 0.25f, cy + t * 0.12f - step * t * LEG_SWING))
    drawCircle(p.trim, t * 0.065f, Offset(cx + t * 0.25f, cy + t * 0.12f + step * t * LEG_SWING))
    // Helmet: a big round shell with ear pads and a light crest stripe.
    val head = Offset(cx, cy - t * 0.12f)
    val headR = t * 0.24f
    drawCircle(dark, t * 0.07f, Offset(head.x - headR, head.y + t * 0.02f))
    drawCircle(dark, t * 0.07f, Offset(head.x + headR, head.y + t * 0.02f))
    drawCircle(look.suit, headR, head)
    drawRoundRect(light, Offset(head.x - t * 0.03f, head.y - headR), Size(t * 0.06f, t * 0.14f), CornerRadius(t * 0.03f))
    visor(head, headR, t, look.facing, p)
    if (look.shielded) drawCircle(p.eye.copy(alpha = SHIELD_ALPHA), t * 0.48f, Offset(cx, cy), style = Stroke(t * 0.04f))
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
    val w = if (facing == BlastDir.DOWN) headR * 1.5f else headR * 1.2f
    val h = headR * 0.75f
    val left = head.x - w / 2 + shift
    val top = head.y - h / 2 + headR * 0.1f
    drawRoundRect(p.visor, Offset(left, top), Size(w, h), CornerRadius(h / 2))
    val eyeR = t * 0.04f
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
