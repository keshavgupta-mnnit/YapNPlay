package com.kglabs28.yapnplay.appengine.games.citybaazar.ui.components.board

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.CityGroup
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.TileKind

/** A colour group's header colour (the original game's colours). */
@Composable
fun CityGroup.tint(): Color {
    val c = YapNPlayTheme.colors
    return when (this) {
        CityGroup.RED -> c.PlayerRed
        CityGroup.BLUE -> c.PlayerBlue
        CityGroup.GREEN -> c.PlayerGreen
        CityGroup.YELLOW -> c.PlayerYellow
        CityGroup.PURPLE -> c.PlayerPurple
        CityGroup.ORANGE -> c.PlayerOrange
    }
}

/** A tile's header colour: its group's, or grey for tiles without one. */
@Composable
fun tileHeaderTint(group: CityGroup?, kind: TileKind): Color =
    group?.tint() ?: if (kind == TileKind.TAX) YapNPlayTheme.colors.Warning else YapNPlayTheme.colors.TextSecondary
