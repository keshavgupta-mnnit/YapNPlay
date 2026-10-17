package com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.board

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.games.citybazaar.domain.CityBazaarColour
import com.kglabs28.yapnplay.appengine.games.citybazaar.utils.CityGroup

/** Every colour the board painter needs (it can't read the theme itself). */
data class CityBazaarBoardColours(
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
    val highlight: Color,
    val players: Map<CityBazaarColour, Color>,
    val house: Color,
    val hotel: Color,
    val chest: Color,
    val luck: Color,
    val start: Color,
    val jail: Color,
)

@Composable
fun cityBazaarBoardColours(): CityBazaarBoardColours {
    val c = YapNPlayTheme.colors
    return CityBazaarBoardColours(
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
        highlight = c.Cyan,
        players = CityBazaarColour.entries.associateWith { it.tint() },
        house = c.CbHouse,
        hotel = c.CbHotel,
        chest = c.CbChest,
        luck = c.CbLuck,
        start = c.CbGain,
        jail = c.CbLuck,
    )
}
