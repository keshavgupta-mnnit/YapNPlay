package com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.dialogs

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp

/**
 * A soft [color] glow round a rounded card's rim, drawn outside it. Used
 * instead of a coloured elevation shadow, which shows through a see-through
 * card as a dark box.
 */
fun Modifier.cbRimGlow(color: Color, cornerRadius: Dp): Modifier = drawBehind {
    val radius = cornerRadius.toPx()
    GLOW.forEach { (spread, alpha) ->
        val grow = spread * size.minDimension
        drawRoundRect(
            color,
            topLeft = Offset(-grow / 2, -grow / 2),
            size = Size(size.width + grow, size.height + grow),
            cornerRadius = CornerRadius(radius + grow / 2),
            alpha = alpha,
            style = Stroke(grow / 2),
        )
    }
}

/** (spread × the card's shorter side, alpha), widest and faintest first. */
private val GLOW = listOf(0.08f to 0.12f, 0.05f to 0.2f, 0.02f to 0.35f)
