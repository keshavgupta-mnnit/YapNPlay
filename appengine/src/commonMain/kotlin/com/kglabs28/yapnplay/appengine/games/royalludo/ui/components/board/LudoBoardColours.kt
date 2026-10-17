package com.kglabs28.yapnplay.appengine.games.royalludo.ui.components.board

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme

/**
 * Theme colours handed to the board painters (DrawScope can't read the
 * theme). [players] is indexed by engine colour: red, green, yellow, blue.
 */
data class LudoBoardColours(
    val top: Color,
    val bottom: Color,
    val side: Color,
    val rim: Color,
    val cell: Color,
    val cellSide: Color,
    val cellEdge: Color,
    val basePad: Color,
    val centre: Color,
    val players: List<Color>,
)

@Composable
fun ludoBoardColours(): LudoBoardColours {
    val c = YapNPlayTheme.colors
    return LudoBoardColours(
        top = c.LudoBoardTop,
        bottom = c.LudoBoardBottom,
        side = c.LudoBoardSide,
        rim = c.LudoBoardRim,
        cell = c.LudoCell,
        cellSide = c.LudoCellSide,
        cellEdge = c.LudoCellEdge,
        basePad = c.LudoBasePad,
        centre = c.LudoCentrePlate,
        players = listOf(c.PlayerRed, c.PlayerGreen, c.PlayerYellow, c.PlayerBlue),
    )
}
