package com.kglabs28.yapnplay.appengine.games.citybaazar.ui.components.board

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.games.citybaazar.domain.CityBaazarColour
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.CityGroup

/** Every colour the board painter needs (it can't read the theme itself). */
data class CityBaazarBoardColours(
    val slabTop: Color,
    val slabBottom: Color,
    val slabSide: Color,
    val rim: Color,
    val field: Color,
    val tile: Color,
    val tileEdge: Color,
    val tileSide: Color,
    val text: Color,
    val subText: Color,
    val groups: Map<CityGroup, Color>,
    val transit: Color,
    val utility: Color,
    val players: Map<CityBaazarColour, Color>,
    val house: Color,
    val hotel: Color,
    val chest: Color,
    val luck: Color,
    val start: Color,
    val jail: Color,
)

@Composable
fun cityBaazarBoardColours(): CityBaazarBoardColours {
    val c = YapNPlayTheme.colors
    return CityBaazarBoardColours(
        slabTop = c.LudoBoardTop,
        slabBottom = c.LudoBoardBottom,
        slabSide = c.LudoBoardSide,
        rim = c.LudoBoardRim,
        field = c.CbField,
        tile = c.CbTile,
        tileEdge = c.CbTileEdge,
        tileSide = c.CbTileSide,
        text = c.TextPrimary,
        subText = c.TextSecondary,
        groups = CityGroup.entries.associateWith { it.tint() },
        transit = c.CbTransit,
        utility = c.CbUtility,
        players = CityBaazarColour.entries.associateWith { it.tint() },
        house = c.CbHouse,
        hotel = c.CbHotel,
        chest = c.CbChest,
        luck = c.CbLuck,
        start = c.CbGain,
        jail = c.CbLuck,
    )
}
