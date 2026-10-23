package com.kglabs28.yapnplay.appengine.ui.components.table

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Constraints
import kotlin.math.roundToInt

/** Draws the content at [scale] of its size, taking only that much room (anchored top-left). */
fun Modifier.monoScaled(scale: Float): Modifier = if (scale == 1f) this else layout { measurable, constraints ->
    val placeable = measurable.measure(
        Constraints(
            maxWidth = if (constraints.hasBoundedWidth) (constraints.maxWidth / scale).toInt() else Constraints.Infinity,
            maxHeight = if (constraints.hasBoundedHeight) (constraints.maxHeight / scale).toInt() else Constraints.Infinity,
        ),
    )
    layout((placeable.width * scale).roundToInt(), (placeable.height * scale).roundToInt()) {
        placeable.placeWithLayer(0, 0) {
            scaleX = scale
            scaleY = scale
            transformOrigin = TransformOrigin(0f, 0f)
        }
    }
}
