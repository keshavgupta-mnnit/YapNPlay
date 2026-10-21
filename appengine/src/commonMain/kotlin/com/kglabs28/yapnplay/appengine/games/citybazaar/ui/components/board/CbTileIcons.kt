package com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.board

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Celebration
import androidx.compose.material.icons.rounded.DirectionsBoat
import androidx.compose.material.icons.rounded.DirectionsBus
import androidx.compose.material.icons.rounded.Flag
import androidx.compose.material.icons.rounded.Flight
import androidx.compose.material.icons.rounded.Gavel
import androidx.compose.material.icons.rounded.Hotel
import androidx.compose.material.icons.rounded.Receipt
import androidx.compose.material.icons.rounded.Train
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter

/**
 * The picture on each non-city tile: what the company is (train, bus,
 * plane, boat, water, power), the two taxes, and the four corners.
 * Cities have none (their colour header says enough).
 */
object CbTileIcons {
    fun vector(tile: Int): ImageVector? = when (tile) {
        START -> Icons.Rounded.Flag
        WATER_WORKS -> Icons.Rounded.WaterDrop
        RAILWAYS -> Icons.Rounded.Train
        INCOME_TAX -> Icons.Rounded.Receipt
        JAIL -> Icons.Rounded.Gavel
        ELECTRIC -> Icons.Rounded.Bolt
        BEST -> Icons.Rounded.DirectionsBus
        CLUB -> Icons.Rounded.Celebration
        AIR_INDIA -> Icons.Rounded.Flight
        REST_HOUSE -> Icons.Rounded.Hotel
        WEALTH_TAX -> Icons.Rounded.AccountBalance
        MOTORBOAT -> Icons.Rounded.DirectionsBoat
        else -> null
    }

    private const val START = 0
    private const val WATER_WORKS = 2
    private const val RAILWAYS = 3
    private const val INCOME_TAX = 5
    private const val JAIL = 9
    private const val ELECTRIC = 12
    private const val BEST = 13
    private const val CLUB = 18
    private const val AIR_INDIA = 24
    private const val REST_HOUSE = 27
    private const val WEALTH_TAX = 31
    private const val MOTORBOAT = 34
    private const val TILE_COUNT = 36
    internal val tiles: IntRange get() = 0 until TILE_COUNT
}

/** Painters for every tile that has an icon (tile index → painter), for the board painters. */
@Composable
fun rememberCbTileIcons(): Map<Int, Painter> =
    CbTileIcons.tiles.mapNotNull { tile -> CbTileIcons.vector(tile)?.let { tile to rememberVectorPainter(it) } }.toMap()
