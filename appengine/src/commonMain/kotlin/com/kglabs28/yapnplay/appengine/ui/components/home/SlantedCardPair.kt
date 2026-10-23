package com.kglabs28.yapnplay.appengine.ui.components.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import kotlin.math.roundToInt

/**
 * Two cards side by side whose facing edges lean the same way, the start
 * card's by [startSlant]. The gap between them is [gap] at the top; when
 * the end card leans further (its own shape), the gap widens toward the
 * bottom.
 */
@Composable
fun SlantedCardPair(
    startSlant: Dp,
    gap: Dp,
    start: @Composable () -> Unit,
    end: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    Layout(contents = listOf(start, end), modifier = modifier) { (startCard, endCard), constraints ->
        val width = constraints.maxWidth
        val cardWidth = ((width + startSlant.toPx() - gap.toPx()) / 2).roundToInt().coerceIn(0, width)
        val cardConstraints = Constraints.fixed(cardWidth, constraints.maxHeight)
        val left = startCard.first().measure(cardConstraints)
        val right = endCard.first().measure(cardConstraints)
        layout(width, constraints.maxHeight) {
            left.place(0, 0)
            right.place(width - cardWidth, 0)
        }
    }
}
