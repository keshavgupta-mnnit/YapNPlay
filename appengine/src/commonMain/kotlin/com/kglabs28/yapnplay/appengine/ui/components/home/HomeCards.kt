package com.kglabs28.yapnplay.appengine.ui.components.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing

/**
 * The Home tab: Game Zone on the left (40% of the width at the top, 55% at
 * the bottom), Play With Friends on the right (50% at the top, 30% at the
 * bottom), the gap between them widening toward the bottom.
 */
@Composable
fun HomeCards(
    onEnterGameZone: () -> Unit,
    onPlayWithFriends: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SlantedCardPair(
        startTop = GAME_ZONE_TOP,
        startBottom = GAME_ZONE_BOTTOM,
        endTop = FRIENDS_TOP,
        endBottom = FRIENDS_BOTTOM,
        modifier = modifier.fillMaxSize().padding(top = Spacing.Xs),
        start = { slant -> EnterGameZoneCard(onEnterGameZone = onEnterGameZone, slant = slant) },
        end = { slant, gameZoneEdge -> PlayWithFriendsCard(onPlayWithFriends = onPlayWithFriends, slant = slant, gameZoneEdge = gameZoneEdge) },
    )
}

/** Each card's share of the width at the cards' top and at their bottom. */
private const val GAME_ZONE_TOP = 0.4f
private const val GAME_ZONE_BOTTOM = 0.55f
private const val FRIENDS_TOP = 0.5f
private const val FRIENDS_BOTTOM = 0.3f
