package com.kglabs28.yapnplay.appengine.ui.placeholders

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import com.kglabs28.yapnplay.appengine.core.designsystem.AdaptiveScale
import kotlin.math.min

/**
 * A square card picture drawn to fit its slot and zoomed past its clear
 * margin: [phoneZoom] on a phone, as designed; on a tablet only as far as
 * the visible art ([visibleWidth] × [visibleHeight] of the image, as
 * shares) still fits inside the slot, so it never runs over the card's
 * title, edge or buttons.
 */
@Composable
fun SlotFittedArt(
    phoneZoom: Float,
    visibleWidth: Float,
    visibleHeight: Float,
    modifier: Modifier = Modifier,
    /** On a tablet, this much bigger than the fitted size (1 = exactly fits). */
    tabletBoost: Float = 1f,
    art: @Composable (Modifier) -> Unit,
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        // The picture is drawn as a square as big as the slot's shorter side.
        val side = min(maxWidth.value, maxHeight.value).coerceAtLeast(1f)
        val fits = min(maxWidth.value / (side * visibleWidth), maxHeight.value / (side * visibleHeight))
        val zoom = AdaptiveScale.forTablet(phone = phoneZoom, tablet = min(phoneZoom, fits) * tabletBoost)
        art(
            Modifier.fillMaxSize().graphicsLayer {
                scaleX = zoom
                scaleY = zoom
            },
        )
    }
}
