package com.kglabs28.yapnplay.appengine.ui.components.common

import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Constraints
import kotlin.math.roundToInt

/**
 * Lays the content out at its own size, then scales it down (never up)
 * so it fits the room it was given: a colour picker, a vote or a note
 * that would be taller than the gap between the piles and the hand
 * shrinks rather than covering the cards.
 */
fun Modifier.fitToSpace(): Modifier = layout { measurable, constraints ->
    val placeable = measurable.measure(Constraints(maxWidth = constraints.maxWidth))
    val fitHeight = if (constraints.hasBoundedHeight && placeable.height > 0) constraints.maxHeight.toFloat() / placeable.height else 1f
    val fitWidth = if (constraints.hasBoundedWidth && placeable.width > 0) constraints.maxWidth.toFloat() / placeable.width else 1f
    val scale = minOf(1f, fitHeight, fitWidth)
    val width = (placeable.width * scale).roundToInt()
    val height = (placeable.height * scale).roundToInt()
    layout(width, height) {
        placeable.placeWithLayer((width - placeable.width) / 2, (height - placeable.height) / 2) {
            scaleX = scale
            scaleY = scale
        }
    }
}
