package com.kglabs28.yapnplay.appengine.ui.placeholders

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.common.AppConstants

/**
 * "PLAY WITH FRIENDS" card graphic. Temporary static art until final
 * assets land — swap the drawable, nothing else changes.
 */
@Composable
fun SocialNetworkOrbitGraphic(
    modifier: Modifier = Modifier,
) {
    // The PNG carries a clear margin; zoomed past it, the art fills the slot (never past it on a tablet).
    SlotFittedArt(phoneZoom = AppConstants.FRIENDS_ART_ZOOM, visibleWidth = FRIENDS_VISIBLE_WIDTH, visibleHeight = FRIENDS_VISIBLE_HEIGHT, modifier = modifier, tabletBoost = TABLET_BOOST) { FriendZoneArtwork(modifier = it) }
}

/** How much of the image the art itself covers (the rest is clear margin), measured. */
private const val FRIENDS_VISIBLE_WIDTH = 0.9f
private const val FRIENDS_VISIBLE_HEIGHT = 0.56f
/** On a tablet the friends art is drawn 10% bigger than its fitted size. */
private const val TABLET_BOOST = 1.1f
