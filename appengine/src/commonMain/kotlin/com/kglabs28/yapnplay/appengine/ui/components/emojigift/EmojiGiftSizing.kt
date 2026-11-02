package com.kglabs28.yapnplay.appengine.ui.components.emojigift

import androidx.compose.ui.unit.Dp

/**
 * The emoji picker's size from the screen: each tile is a share of the
 * window's height (never above its design size), and the grid's width sets
 * the whole picker's (header and ad included), so there is no empty room
 * and three rows stay well short of the screen on any phone.
 */
object EmojiGiftSizing {
    /** One tile, square, for a window [windowHeight] tall. */
    fun tile(windowHeight: Dp, largest: Dp): Dp = (windowHeight * TILE_OF_HEIGHT).coerceAtMost(largest)

    /** The grid's width: [columns] tiles and their gaps. */
    fun width(tile: Dp, columns: Int, gap: Dp): Dp = tile * columns + gap * (columns - 1)

    /** The emoji in a tile, and the coin by its price, against the tile. */
    const val ART_OF_TILE = 0.6f
    const val COIN_OF_TILE = 0.24f

    private const val TILE_OF_HEIGHT = 0.12f
}
