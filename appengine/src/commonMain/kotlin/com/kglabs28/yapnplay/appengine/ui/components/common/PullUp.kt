package com.kglabs28.yapnplay.appengine.ui.components.common

import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Dp

/**
 * Draws this [by] higher than its place and gives that room back to what
 * follows: it tucks into empty space above it (under a top bar whose pills
 * are shorter than its logo) without leaving a gap below.
 */
fun Modifier.pullUp(by: Dp): Modifier = layout { measurable, constraints ->
    val placeable = measurable.measure(constraints)
    val shift = by.roundToPx().coerceAtMost(placeable.height)
    layout(placeable.width, placeable.height - shift) { placeable.place(0, -shift) }
}
