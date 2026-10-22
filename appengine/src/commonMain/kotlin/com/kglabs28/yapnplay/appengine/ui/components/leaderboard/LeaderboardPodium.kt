package com.kglabs28.yapnplay.appengine.ui.components.leaderboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Constraints
import kotlin.math.roundToInt

/**
 * The top three as in the design: a row of three [PodiumPosition]s — 2nd,
 * 1st, 3rd — standing on one platform. All three boxes are always there;
 * a place nobody took has no card on it. Built at [PodiumSizes]; when the
 * space is shorter or narrower, the whole podium shrinks evenly (never
 * one part), anchored to the bottom.
 */
@Composable
fun LeaderboardPodium(podium: List<LeaderboardEntryUi>, modifier: Modifier = Modifier) {
    BoxWithConstraints(modifier = modifier, contentAlignment = Alignment.BottomCenter) {
        val width = PodiumSizes.width * SPOTS
        val height = PodiumSizes.tallest
        val scale = minOf(1f, maxWidth / width, maxHeight / height)
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
            Row(
                // Laid out at its designed size, then drawn [scale] times smaller and reported at that
                // smaller size, so the parent places exactly what's drawn (bottom-aligned).
                modifier = Modifier.layout { measurable, _ ->
                    val w = width.roundToPx()
                    val h = height.roundToPx()
                    val placeable = measurable.measure(Constraints.fixed(w, h))
                    val shownW = (w * scale).roundToInt()
                    val shownH = (h * scale).roundToInt()
                    layout(shownW, shownH) {
                        placeable.placeWithLayer(-(w - shownW) / 2, -(h - shownH) / 2) {
                            scaleX = scale
                            scaleY = scale
                        }
                    }
                },
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center,
            ) {
                PodiumPosition(place = 2, entry = podium.getOrNull(1))
                PodiumPosition(place = 1, entry = podium.getOrNull(0))
                PodiumPosition(place = 3, entry = podium.getOrNull(2))
            }
        }
    }
}

private const val SPOTS = 3
