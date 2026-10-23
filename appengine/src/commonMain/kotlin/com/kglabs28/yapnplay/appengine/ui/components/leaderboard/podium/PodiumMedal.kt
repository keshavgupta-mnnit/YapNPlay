package com.kglabs28.yapnplay.appengine.ui.components.leaderboard.podium

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme

/** Gold, silver, bronze for 1st, 2nd, 3rd. */
@Composable
fun podiumMedal(place: Int): Color = when (place) {
    1 -> YapNPlayTheme.colors.MedalGold
    2 -> YapNPlayTheme.colors.MedalSilver
    else -> YapNPlayTheme.colors.MedalBronze
}

/** How tall [place]'s box is. */
@Composable
fun podiumBoxHeight(place: Int): Dp = when (place) {
    1 -> YapNPlayTheme.dimens.podiumBoxFirst
    2 -> YapNPlayTheme.dimens.podiumBoxSecond
    else -> YapNPlayTheme.dimens.podiumBoxThird
}
