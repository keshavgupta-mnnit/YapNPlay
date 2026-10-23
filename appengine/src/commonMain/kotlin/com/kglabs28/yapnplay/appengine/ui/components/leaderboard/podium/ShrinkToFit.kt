package com.kglabs28.yapnplay.appengine.ui.components.leaderboard.podium

import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import kotlin.math.roundToInt

/**
 * Lays the content out at [width] × [height], draws it [fit] times as big
 * (1 = as is) and reports that smaller size, so the parent places exactly
 * what is drawn.
 */
fun Modifier.shrinkToFit(fit: Float, width: Dp, height: Dp): Modifier = layout { measurable, _ ->
    val w = width.roundToPx()
    val h = height.roundToPx()
    val placeable = measurable.measure(Constraints.fixed(w, h))
    val shownW = (w * fit).roundToInt()
    val shownH = (h * fit).roundToInt()
    layout(shownW, shownH) {
        placeable.placeWithLayer(-(w - shownW) / 2, -(h - shownH) / 2) {
            scaleX = fit
            scaleY = fit
        }
    }
}
