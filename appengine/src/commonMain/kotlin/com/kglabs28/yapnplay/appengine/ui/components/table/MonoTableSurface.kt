package com.kglabs28.yapnplay.appengine.ui.components.table

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * The table the two centre piles sit on, as in the table design: a dark
 * oval of felt with a lit centre, ringed by a glowing cyan rim.
 */
@Composable
fun MonoTableSurface(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    val colors = YapNPlayTheme.colors
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val inset = size.minDimension * OVAL_INSET
            val oval = Size(size.width - inset * 2, size.height - inset * 2)
            val topLeft = Offset(inset, inset)
            drawOval(
                Brush.radialGradient(listOf(colors.MonoTableFelt, colors.MonoTableFeltEdge), center, size.width / 2),
                topLeft = topLeft,
                size = oval,
            )
            // The cyan rim: a wide soft glow, the lit line, and a fine steel edge outside it.
            drawOval(colors.Cyan.copy(alpha = GLOW_ALPHA), topLeft, oval, style = Stroke(size.minDimension * GLOW_STROKE))
            drawOval(colors.Cyan.copy(alpha = RIM_ALPHA), topLeft, oval, style = Stroke(size.minDimension * RIM_STROKE))
            val outer = size.minDimension * EDGE_GAP
            drawOval(
                colors.MonoTableRim,
                Offset(inset - outer, inset - outer),
                Size(oval.width + outer * 2, oval.height + outer * 2),
                style = Stroke(size.minDimension * EDGE_STROKE),
            )
        }
        content()
    }
}

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
    Canvas(modifier = modifier) {
        val inset = size.minDimension * (OVAL_INSET + ARROW_INSET)
        val rx = size.width / 2 - inset
        val ry = size.height / 2 - inset
        val stroke = size.minDimension * ARROW_STROKE
        // Right side, then left side: each sweeps through its own half.
        listOf(RIGHT_START, RIGHT_START + HALF_TURN).forEach { start ->
            drawTurnArc(center, rx, ry, start, ARC_SWEEP, clockwise, stroke, ring)
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
private const val GLOW_STROKE = 0.05f
private const val GLOW_ALPHA = 0.28f
private const val RIM_STROKE = 0.012f
private const val RIM_ALPHA = 0.9f
private const val EDGE_GAP = 0.03f
private const val EDGE_STROKE = 0.006f
private const val ARROW_INSET = 0.1f
private const val ARROW_STROKE = 0.022f
private const val ARROW_GLOW = 3f
private const val HEAD = 2.4f
private const val HEAD_WIDE = 0.9f
/** The right arc runs from upper right to lower right (screen degrees). */
private const val RIGHT_START = -40f
private const val ARC_SWEEP = 80f
private const val HALF_TURN = 180f
