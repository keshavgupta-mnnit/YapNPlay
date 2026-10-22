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
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * The direction arrows inside the oval: two curved arrows, one down each
 * side, chasing each other round the piles and flipping when the turn
 * order reverses. They are in the colour in force; like a keyboard's
 * light chase, a bright band runs along each arrow from its tail to its
 * head, over and over, leaving a fading trail, so the direction reads at
 * a glance. The arrowhead flares as the band reaches it.
 */
@Composable
fun MonoTurnRing(
    hue: MonoHue?,
    clockwise: Boolean,
    modifier: Modifier = Modifier,
) {
    // 0 → 1 over one run of the wave.
    val phase by rememberInfiniteTransition(label = "turn-ring").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(WAVE_MS, easing = LinearEasing)),
        label = "turn-ring-wave",
    )
    val base = hue.ring
    Canvas(modifier = modifier) {
        val inset = size.minDimension * (OVAL_INSET + ARROW_INSET)
        val rx = size.width / 2 - inset
        val ry = size.height / 2 - inset
        val stroke = size.minDimension * ARROW_STROKE
        // Right side, then left side: each sweeps through its own half.
        listOf(RIGHT_START, RIGHT_START + HALF_TURN).forEach { start ->
            drawWaveArc(center, rx, ry, start, ARC_SWEEP, clockwise, stroke, base, phase)
        }
    }
}

/** One arc as a run of short segments, each coloured by where the wave is; an arrowhead at the leading end. */
private fun DrawScope.drawWaveArc(c: Offset, rx: Float, ry: Float, startDeg: Float, sweepDeg: Float, clockwise: Boolean, stroke: Float, base: Color, phase: Float) {
    val box = Offset(c.x - rx, c.y - ry)
    val oval = Size(rx * 2, ry * 2)
    val step = sweepDeg / SEGMENTS
    var headColour = base
    for (i in 0 until SEGMENTS) {
        // Position along the arrow, tail (0) to head (1).
        val along = if (clockwise) i / (SEGMENTS - 1f) else 1f - i / (SEGMENTS - 1f)
        val colour = chaseColour(base, along, phase)
        // A soft glow under a bright core; the segments overlap a little so the line stays unbroken.
        drawArc(colour.copy(alpha = GLOW_ALPHA), startDeg + i * step, step + SEG_OVERLAP, false, box, oval, style = Stroke(stroke * ARROW_GLOW, cap = StrokeCap.Butt))
        drawArc(colour, startDeg + i * step, step + SEG_OVERLAP, false, box, oval, style = Stroke(stroke, cap = StrokeCap.Butt))
        if (along >= 1f - 1f / SEGMENTS) headColour = colour
    }
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
    drawPath(path, headColour)
}

/**
 * The colour at [along] (0 tail … 1 head) when the band is at [phase]: the
 * arrow's own [base], brightened towards white where the band is and
 * fading along the trail behind it.
 */
private fun chaseColour(base: Color, along: Float, phase: Float): Color {
    // How far behind the band this point is (0 = in it), wrapping round.
    val behind = (phase * CHASE_RUN - along).mod(CHASE_RUN)
    val lit = (1f - behind / CHASE_TRAIL).coerceIn(0f, 1f)
    val bright = lerp(base, Color.White, CHASE_WHITE)
    return lerp(base.copy(alpha = CHASE_DIM), bright, lit)
}

/** Fractions of the arena's shorter side. */
private const val OVAL_INSET = 0.04f
private const val ARROW_INSET = 0.1f
private const val ARROW_STROKE = 0.04f
private const val ARROW_GLOW = 2.4f
private const val GLOW_ALPHA = 0.28f
private const val HEAD = 2.4f
private const val HEAD_WIDE = 0.9f
/** The right arc runs from upper right to lower right (screen degrees). */
private const val RIGHT_START = -40f
private const val ARC_SWEEP = 80f
private const val HALF_TURN = 180f
/**
 * The chase: segments per arrow; how far the band runs per cycle (past the
 * head, so the trail clears before it starts again); how long its trail is
 * (fraction of the arrow); how much it whitens; how dim the rest is; one
 * cycle's length.
 */
private const val SEGMENTS = 28
private const val SEG_OVERLAP = 0.6f
private const val CHASE_RUN = 1.5f
private const val CHASE_TRAIL = 0.55f
private const val CHASE_WHITE = 0.55f
private const val CHASE_DIM = 0.55f
private const val WAVE_MS = 1300
