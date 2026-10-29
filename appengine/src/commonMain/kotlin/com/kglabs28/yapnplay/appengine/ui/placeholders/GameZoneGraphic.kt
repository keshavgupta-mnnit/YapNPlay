package com.kglabs28.yapnplay.appengine.ui.placeholders

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.common.AppConstants

/**
 * "ENTER GAME ZONE" card graphic. Temporary static art until final
 * assets land — swap the drawable, nothing else changes.
 */
@Composable
fun GameZoneGraphic(
    modifier: Modifier = Modifier,
) {
    // The PNG carries a clear margin; zoomed past it, the art fills the slot (never past it on a tablet).
    SlotFittedArt(phoneZoom = AppConstants.CARD_ART_ZOOM, visibleWidth = GAME_ZONE_VISIBLE_WIDTH, visibleHeight = GAME_ZONE_VISIBLE_HEIGHT, modifier = modifier) { GameZoneArtwork(modifier = it) }
}

/** How much of the image the art itself covers (the rest is clear margin), measured. */
private const val GAME_ZONE_VISIBLE_WIDTH = 0.88f
private const val GAME_ZONE_VISIBLE_HEIGHT = 0.82f
