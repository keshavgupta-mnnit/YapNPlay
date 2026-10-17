package com.kglabs28.yapnplay.appengine.games.royalludo.ui.components.glass

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp

/**
 * The turn timer drawn as the card's border: a solid [tint] line round
 * the rounded rectangle, ending at the top centre; it shrinks clockwise
 * (its start chases round toward the top) as [fraction] goes from 1 to 0. Nothing is drawn when it is not this
 * player's turn ([active] false).
 */
fun Modifier.turnTimerBorder(active: Boolean, fraction: Float, tint: Color, radius: Dp, width: Dp): Modifier =
    drawWithContent {
        drawContent()
        if (!active) return@drawWithContent
        val stroke = width.toPx()
        val outline = outline(size, radius.toPx(), stroke / 2)
        drawPath(outline, tint.copy(alpha = TRACK_ALPHA), style = Stroke(stroke))
        val measure = PathMeasure().apply { setPath(outline, false) }
        val left = Path()
        measure.getSegment(measure.length * (1f - fraction.coerceIn(0f, 1f)), measure.length, left, true)
        drawPath(left, tint, style = Stroke(stroke, cap = StrokeCap.Round))
    }

/** Rounded-rectangle outline starting at the top centre, going clockwise. */
private fun outline(size: Size, radius: Float, inset: Float): Path {
    val l = inset
    val t = inset
    val r = size.width - inset
    val b = size.height - inset
    val d = radius * 2
    return Path().apply {
        moveTo(size.width / 2, t)
        lineTo(r - radius, t)
        arcTo(Rect(r - d, t, r, t + d), -QUARTER, QUARTER, false)
        lineTo(r, b - radius)
        arcTo(Rect(r - d, b - d, r, b), 0f, QUARTER, false)
        lineTo(l + radius, b)
        arcTo(Rect(l, b - d, l + d, b), QUARTER, QUARTER, false)
        lineTo(l, t + radius)
        arcTo(Rect(l, t, l + d, t + d), 2 * QUARTER, QUARTER, false)
        lineTo(size.width / 2, t)
    }
}

private const val QUARTER = 90f
private const val TRACK_ALPHA = 0.25f
