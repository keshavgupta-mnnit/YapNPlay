package com.kglabs28.yapnplay.appengine.games.citybaazar.ui.components.board

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.CityGroup
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.TileKind

/** A colour group's header colour. */
@Composable
fun CityGroup.tint(): Color {
    val c = YapNPlayTheme.colors
    return when (this) {
        CityGroup.BROWN -> c.CbGroupBrown
        CityGroup.SKY -> c.CbGroupSky
        CityGroup.PINK -> c.CbGroupPink
        CityGroup.AMBER -> c.CbGroupAmber
        CityGroup.CRIMSON -> c.CbGroupCrimson
        CityGroup.LIME -> c.CbGroupLime
        CityGroup.TEAL -> c.CbGroupTeal
        CityGroup.INDIGO -> c.CbGroupIndigo
    }
}

/** A tile's header colour: its group, or transit / utility grey. */
@Composable
fun tileHeaderTint(group: CityGroup?, kind: TileKind): Color = group?.tint() ?: when (kind) {
    TileKind.TRANSIT -> YapNPlayTheme.colors.CbTransit
    TileKind.UTILITY -> YapNPlayTheme.colors.CbUtility
    else -> YapNPlayTheme.colors.TextSecondary
}
