package com.kglabs28.yapnplay.appengine.ui.components.table

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

/**
 * Three signal bars, short to tall, the first [lit] of them in [tint] and
 * the rest faint: always the full set, so a weak signal never looks cut off.
 */
@Composable
fun SignalBars(lit: Int, tint: Color, size: Dp, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(size)) {
        val gap = this.size.width * GAP
        val bar = (this.size.width - gap * (BARS - 1)) / BARS
        repeat(BARS) { i ->
            val height = this.size.height * (i + 1) / BARS
            drawRoundRect(
                color = if (i < lit) tint else tint.copy(alpha = FAINT),
                topLeft = Offset(i * (bar + gap), this.size.height - height),
                size = Size(bar, height),
                cornerRadius = CornerRadius(bar / 3),
            )
        }
    }
}

private const val BARS = 3
private const val GAP = 0.14f
private const val FAINT = 0.25f
