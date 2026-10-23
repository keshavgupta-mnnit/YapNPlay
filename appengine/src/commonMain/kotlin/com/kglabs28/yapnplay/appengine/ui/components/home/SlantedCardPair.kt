package com.kglabs28.yapnplay.appengine.ui.components.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

/**
 * Two cards side by side with leaning facing edges. The start card spans
 * [startTop] of the width at the top and [startBottom] at the bottom; the
 * end card [endTop] and [endBottom] (from the right). What's left between
 * them is the gap, which may widen toward the bottom. The start card is told
 * how far its edge leans; the end card how far its own leans, and where the
 * start card's bottom corner sits in its own coordinates (so anything it
 * pushes out past its edge stays clear of the start card).
 */
@Composable
fun SlantedCardPair(
    startTop: Float,
    startBottom: Float,
    endTop: Float,
    endBottom: Float,
    start: @Composable (slant: Dp) -> Unit,
    end: @Composable (slant: Dp, startCardEdge: Dp) -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier = modifier) {
        val width = maxWidth
        Box(Modifier.width(width * startBottom).fillMaxHeight()) { start(width * (startBottom - startTop)) }
        Box(Modifier.align(Alignment.TopEnd).width(width * endTop).fillMaxHeight()) {
            end(width * (endTop - endBottom), width * (startBottom - (1 - endTop)))
        }
    }
}
