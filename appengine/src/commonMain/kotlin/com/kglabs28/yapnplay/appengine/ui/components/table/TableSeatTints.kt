package com.kglabs28.yapnplay.appengine.ui.components.table

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme

/** Each seat's colour in games that colour players by seat (Doodle King, Cyber Blast): card rim, names, hearts, fire; up to 8 seats. */
@Composable
fun tableSeatTints(): List<Color> {
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
