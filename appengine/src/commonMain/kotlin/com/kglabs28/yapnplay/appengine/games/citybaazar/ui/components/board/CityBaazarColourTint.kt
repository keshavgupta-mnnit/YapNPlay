package com.kglabs28.yapnplay.appengine.games.citybaazar.ui.components.board

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.games.citybaazar.domain.CityBaazarColour

/** A player's neon colour (token, card, owned-tile border). */
@Composable
fun CityBaazarColour.tint(): Color {
    val c = YapNPlayTheme.colors
    return when (this) {
        CityBaazarColour.RED -> c.PlayerRed
        CityBaazarColour.GREEN -> c.PlayerGreen
        CityBaazarColour.YELLOW -> c.PlayerYellow
        CityBaazarColour.BLUE -> c.PlayerBlue
        CityBaazarColour.PURPLE -> c.PlayerPurple
        CityBaazarColour.ORANGE -> c.PlayerOrange
    }
}
