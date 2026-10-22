package com.kglabs28.yapnplay.appengine.ui.components.leaderboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.min
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme

/**
 * The top three, each the same player card on a medal cube, as in the design: 2nd, 1st (centre,
 * tallest, crowned), 3rd. Every size comes from the space given: the boxes
 * are fixed shares of the height (1st > 2nd > 3rd) and the avatars shrink
 * with it, so 1st's box is always there however small the screen.
 */
@Composable
fun LeaderboardPodium(podium: List<LeaderboardEntryUi>, modifier: Modifier = Modifier) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    BoxWithConstraints(modifier = modifier) {
        val spotWidth = min(dimens.boardSpotWidth * SPOT_GROW, maxWidth / SPOTS)
        // As in the design, all three avatars are the same size.
        val avatar = min(dimens.boardAvatarFirst * AVATAR_GROW, maxHeight * AVATAR_SHARE)
        val firstBase = maxHeight * FIRST_BASE_SHARE
        val secondBase = maxHeight * SECOND_BASE_SHARE
        val thirdBase = maxHeight * THIRD_BASE_SHARE
        // Every player's card is the same height, whatever their place.
        val card = maxHeight * CARD_SHARE
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.Bottom,
            // The steps touch, forming one platform.
            horizontalArrangement = Arrangement.Center,
        ) {
            podium.getOrNull(1)?.let { PodiumSpot(entry = it, medal = colors.MedalSilver, avatarSize = avatar, cardHeight = card, cubeHeight = secondBase, modifier = Modifier.width(spotWidth)) }
            podium.getOrNull(0)?.let { PodiumSpot(entry = it, medal = colors.MedalGold, avatarSize = avatar, cardHeight = card, cubeHeight = firstBase, modifier = Modifier.width(spotWidth)) }
            podium.getOrNull(2)?.let { PodiumSpot(entry = it, medal = colors.MedalBronze, avatarSize = avatar, cardHeight = card, cubeHeight = thirdBase, modifier = Modifier.width(spotWidth)) }
        }
    }
}

private const val SPOTS = 3
private const val AVATAR_GROW = 1.2f
private const val AVATAR_SHARE = 0.26f
/** Every podium card's height ÷ the podium's height. */
private const val CARD_SHARE = 0.3f
private const val SPOT_GROW = 1f
// 10% taller than before; the cube's top face sits on top of these.
private const val FIRST_BASE_SHARE = 0.352f
private const val SECOND_BASE_SHARE = 0.264f
private const val THIRD_BASE_SHARE = 0.176f
