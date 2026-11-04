package com.kglabs28.yapnplay.appengine.games.doodleking.ui.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.unit.Dp

/**
 * A soft glow of [color] just outside a pill-shaped element, fading out over
 * [reach]. Only the outside is drawn (the element's own inside stays clear),
 * so it never tints or boxes what sits in it.
 */
fun Modifier.pillGlow(color: Color, reach: Dp): Modifier = drawBehind {
    val outer = reach.toPx()
    val radius = size.height / 2
    val pill = Path().apply { addRoundRect(RoundRect(Rect(Offset.Zero, size), CornerRadius(radius))) }
    clipPath(pill, ClipOp.Difference) {
        for (step in GLOW_STEPS downTo 1) {
            val reachHere = outer * step / GLOW_STEPS
            drawRoundRect(
                color = color.copy(alpha = GLOW_ALPHA),
                topLeft = Offset.Zero,
                size = size,
                cornerRadius = CornerRadius(radius),
                style = Stroke(width = reachHere * 2),
            )
        }
    }
}

private const val GLOW_STEPS = 5
private const val GLOW_ALPHA = 0.1f
