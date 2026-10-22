package com.kglabs28.yapnplay.appengine.ui.components.table

import androidx.compose.foundation.Canvas
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * The direction arrows inside the oval: two curved arrows, one down each
 * side, chasing each other round the piles. They take the colour in force
 * and flip when the turn order reverses.
 */
@Composable
fun MonoTurnRing(
    hue: MonoHue?,
    clockwise: Boolean,
    modifier: Modifier = Modifier,
) {
    val ring = hue.ring
    // A glowing dot runs along each arrow from its tail to its head, over and over.
    val travel by rememberInfiniteTransition(label = "turn-ring").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(DOT_MS, easing = LinearEasing)),
        label = "turn-ring-dot",
    )
    Canvas(modifier = modifier) {
        val inset = size.minDimension * (OVAL_INSET + ARROW_INSET)
        val rx = size.width / 2 - inset
        val ry = size.height / 2 - inset
        val stroke = size.minDimension * ARROW_STROKE
        // Right side, then left side: each sweeps through its own half.
        listOf(RIGHT_START, RIGHT_START + HALF_TURN).forEach { start ->
            drawTurnArc(center, rx, ry, start, ARC_SWEEP, clockwise, stroke, ring)
            // Tail to head: clockwise the head is at the sweep's end, otherwise at its start.
            val along = if (clockwise) travel else 1f - travel
            val t = (start + ARC_SWEEP * along) * PI.toFloat() / HALF_TURN
            val dot = Offset(center.x + rx * cos(t), center.y + ry * sin(t))
            val fade = sin(PI.toFloat() * travel)
            drawCircle(Color.White.copy(alpha = DOT_GLOW_ALPHA * fade), stroke * DOT_GLOW, dot)
            drawCircle(Color.White.copy(alpha = fade), stroke * DOT_CORE, dot)
        }
    }
}

/** One arc of the ellipse with an arrowhead at its leading end. */
private fun DrawScope.drawTurnArc(c: Offset, rx: Float, ry: Float, startDeg: Float, sweepDeg: Float, clockwise: Boolean, stroke: Float, colour: Color) {
    drawArc(
        colour.copy(alpha = GLOW_ALPHA),
        startDeg, sweepDeg, false, Offset(c.x - rx, c.y - ry), Size(rx * 2, ry * 2),
        style = Stroke(stroke * ARROW_GLOW, cap = StrokeCap.Round),
    )
    drawArc(colour, startDeg, sweepDeg, false, Offset(c.x - rx, c.y - ry), Size(rx * 2, ry * 2), style = Stroke(stroke, cap = StrokeCap.Round))
    // Clockwise (screen angles grow clockwise) the head sits at the sweep's end; otherwise at its start.
    val headDeg = if (clockwise) startDeg + sweepDeg else startDeg
    val t = headDeg * PI.toFloat() / HALF_TURN
    val tip = Offset(c.x + rx * cos(t), c.y + ry * sin(t))
    // The tangent of the ellipse, pointing the way play runs.
    val dir = if (clockwise) 1f else -1f
    val tx = -rx * sin(t) * dir
    val ty = ry * cos(t) * dir
    val len = kotlin.math.sqrt(tx * tx + ty * ty)
    val ux = tx / len
    val uy = ty / len
    val head = stroke * HEAD
    val path = Path().apply {
        moveTo(tip.x + ux * head, tip.y + uy * head)
        lineTo(tip.x - uy * head * HEAD_WIDE, tip.y + ux * head * HEAD_WIDE)
        lineTo(tip.x + uy * head * HEAD_WIDE, tip.y - ux * head * HEAD_WIDE)
        close()
    }
    drawPath(path, colour)
}

/** Fractions of the arena's shorter side. */
private const val OVAL_INSET = 0.04f
private const val GLOW_ALPHA = 0.28f
private const val ARROW_INSET = 0.1f
private const val ARROW_STROKE = 0.04f
private const val ARROW_GLOW = 2.4f
/** The travelling dot: one run per this many ms, its core and glow × the arrow's width. */
private const val DOT_MS = 1400
private const val DOT_CORE = 0.75f
private const val DOT_GLOW = 2f
private const val DOT_GLOW_ALPHA = 0.45f
private const val HEAD = 2.4f
private const val HEAD_WIDE = 0.9f
/** The right arc runs from upper right to lower right (screen degrees). */
private const val RIGHT_START = -40f
private const val ARC_SWEEP = 80f
private const val HALF_TURN = 180f
