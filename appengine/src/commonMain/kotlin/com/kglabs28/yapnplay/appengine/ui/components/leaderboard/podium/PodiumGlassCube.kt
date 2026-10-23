package com.kglabs28.yapnplay.appengine.ui.components.leaderboard.podium

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke

/**
 * The outer glass cube of a podium box, in [tint]: a lit top face ([top] px
 * deep) and the front, both see-through. Where a neighbour stands the top
 * face runs straight up (no slant), so neighbouring cubes meet without a
 * gap and the three merge into one platform; only the open sides
 * ([showLeft], [showRight]) slant back, get an edge and round their bottom
 * corner ([corner] px).
 */
fun DrawScope.drawPodiumGlassCube(tint: Color, showLeft: Boolean, showRight: Boolean, top: Float, corner: Float) {
    val w = size.width
    val h = size.height
    val inset = w * TOP_INSET
    val leftInset = if (showLeft) inset else 0f
    val rightInset = if (showRight) inset else 0f
    val face = Path().apply { moveTo(leftInset, 0f); lineTo(w - rightInset, 0f); lineTo(w, top); lineTo(0f, top); close() }
    drawPath(face, Brush.verticalGradient(listOf(tint.copy(alpha = TOP_FAR), tint.copy(alpha = TOP_NEAR)), 0f, top))
    val front = Path().apply {
        addRoundRect(
            RoundRect(
                0f, top, w, h,
                bottomLeftCornerRadius = CornerRadius(if (showLeft) corner else 0f),
                bottomRightCornerRadius = CornerRadius(if (showRight) corner else 0f),
            ),
        )
    }
    drawPath(front, Brush.verticalGradient(listOf(tint.copy(alpha = FRONT_TOP), tint.copy(alpha = FRONT_BOTTOM)), top, h))
    // A sheen across the glass's upper half.
    drawPath(front, Brush.verticalGradient(listOf(Color.White.copy(alpha = SHEEN), Color.Transparent), top, top + (h - top) / 2))
    val line = LINE * density
    // The near edge of the top face.
    drawLine(Color.White.copy(alpha = RIDGE), Offset(0f, top), Offset(w, top), line)
    // The bottom edge, turning up the open sides round their rounded corners.
    val edge = Path().apply {
        if (showLeft) {
            moveTo(inset, 0f); lineTo(0f, top); lineTo(0f, h - line / 2)
        } else {
            moveTo(0f, h - line / 2)
        }
        lineTo(w, h - line / 2)
        if (showRight) {
            lineTo(w, top); lineTo(w - inset, 0f)
        }
    }
    drawPath(edge, tint.copy(alpha = RIM), style = Stroke(line, join = StrokeJoin.Round, pathEffect = PathEffect.cornerPathEffect(corner)))
}

/** How much narrower the top face's far edge is (× width). */
private const val TOP_INSET = 0.07f
/** The glass tint at each part, and its lines. */
private const val TOP_FAR = 0.3f
private const val TOP_NEAR = 0.6f
private const val FRONT_TOP = 0.38f
private const val FRONT_BOTTOM = 0.16f
private const val SHEEN = 0.12f
private const val RIM = 0.85f
private const val RIDGE = 0.6f
private const val LINE = 1.5f
