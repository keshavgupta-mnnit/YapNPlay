package com.kglabs28.yapnplay.appengine.ui.placeholders

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import com.kglabs28.yapnplay.appengine.core.common.AppConstants

/**
 * "ENTER GAME ZONE" card graphic. Temporary static art until final
 * assets land — swap the drawable, nothing else changes.
 */
@Composable
fun GameZoneGraphic(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            // The PNG carries ~10% transparent margin; zoom so the art
            // itself fills the slot without spilling past it.
            .graphicsLayer {
                scaleX = AppConstants.CARD_ART_ZOOM
                scaleY = AppConstants.CARD_ART_ZOOM
            },
        contentAlignment = Alignment.Center,
    ) {
        GameZoneArtwork(modifier = Modifier.fillMaxSize())
    }
}
