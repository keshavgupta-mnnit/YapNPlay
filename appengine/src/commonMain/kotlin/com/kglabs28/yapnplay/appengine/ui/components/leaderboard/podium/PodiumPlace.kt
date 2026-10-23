package com.kglabs28.yapnplay.appengine.ui.components.leaderboard.podium

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.ui.components.leaderboard.LeaderboardEntryUi

/**
 * One place on the podium: its player's card (none when nobody took that
 * place) standing on its box. The card comes down over the box's top face,
 * so the box reads as a solid block under it.
 */
@Composable
fun PodiumPlace(place: Int, entry: LeaderboardEntryUi?, modifier: Modifier = Modifier) {
    val dimens = YapNPlayTheme.dimens
    Column(
        modifier = modifier.width(dimens.podiumWidth),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
    ) {
        if (entry != null) {
            PodiumPlayerCard(entry = entry, modifier = Modifier.offset(y = dimens.podiumCardOverTop).zIndex(1f))
        }
        PodiumBox(place = place)
    }
}
