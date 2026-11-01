package com.kglabs28.yapnplay.appengine.games.doodleking.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme

/** Each seat's colour (card rim, names in the feed and on the leaderboard), up to 8 seats. */
@Composable
fun doodleSeatTints(): List<Color> {
    val colors = YapNPlayTheme.colors
    return listOf(
        colors.CbPlayerRed,
        colors.CbPlayerGreen,
        colors.CbPlayerYellow,
        colors.CbPlayerBlue,
        colors.CbPlayerPurple,
        colors.CbPlayerOrange,
        colors.Cyan,
        colors.Pink,
    )
}

fun List<Color>.forSeat(seat: Int): Color = this[(seat - 1).coerceAtLeast(0) % size]
