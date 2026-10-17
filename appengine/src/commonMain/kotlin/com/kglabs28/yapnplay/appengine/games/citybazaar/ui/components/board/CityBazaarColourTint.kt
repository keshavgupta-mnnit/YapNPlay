package com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.board

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.games.citybazaar.domain.CityBazaarColour

/** A player's colour (token, card, owned-tile border): deeper than the city groups' colours. */
@Composable
fun CityBazaarColour.tint(): Color {
    val c = YapNPlayTheme.colors
    return when (this) {
        CityBazaarColour.RED -> c.CbPlayerRed
        CityBazaarColour.GREEN -> c.CbPlayerGreen
        CityBazaarColour.YELLOW -> c.CbPlayerYellow
        CityBazaarColour.BLUE -> c.CbPlayerBlue
        CityBazaarColour.PURPLE -> c.CbPlayerPurple
        CityBazaarColour.ORANGE -> c.CbPlayerOrange
    }
}
