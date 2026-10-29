package com.kglabs28.yapnplay.appengine.ui.components.leaderboard.podium

import com.kglabs28.yapnplay.appengine.core.designsystem.AdaptiveScale
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.ui.components.leaderboard.LeaderboardEntryUi

/**
 * The podium: 2nd, 1st, 3rd side by side, standing on one line at the
 * bottom. Every size comes from Dimens / the typography (already scaled
 * to the screen), so the podium is drawn at those sizes. Only if a very
 * short screen can't hold it does it shrink as a whole to fit — it never
 * grows past its sizes.
 */
@Composable
fun Podium(podium: List<LeaderboardEntryUi>, modifier: Modifier = Modifier) {
    val dimens = YapNPlayTheme.dimens
    val width = dimens.podiumWidth * PLACES
    // The tallest place (1st): crown, the avatar above the card, the card (over the box's top face), the box.
    val height = dimens.podiumCrown + dimens.podiumAvatarAbove + dimens.podiumCardHeight - dimens.podiumCardOverTop + dimens.podiumBoxFirst
    BoxWithConstraints(modifier = modifier, contentAlignment = Alignment.BottomCenter) {
        // Never bigger than drawn on a phone; a tablet's taller screen lets it grow, still fitting its space.
        val fit = minOf(AdaptiveScale.forTablet(phone = 1f, tablet = TABLET_MAX_GROWTH), maxWidth / width, maxHeight / height)
        Row(
            modifier = Modifier.shrinkToFit(fit, width, height),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.Center,
        ) {
            PodiumPlace(place = 2, entry = podium.getOrNull(1))
            PodiumPlace(place = 1, entry = podium.getOrNull(0))
            PodiumPlace(place = 3, entry = podium.getOrNull(2))
        }
    }
}

private const val PLACES = 3

/** How much bigger than on a phone the podium may grow on a tablet. */
private const val TABLET_MAX_GROWTH = 1.5f
