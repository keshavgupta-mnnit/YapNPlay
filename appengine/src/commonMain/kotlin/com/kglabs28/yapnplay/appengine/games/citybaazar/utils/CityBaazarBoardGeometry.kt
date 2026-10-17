package com.kglabs28.yapnplay.appengine.games.citybaazar.utils

/**
 * Board tiles on a 10×10 grid ring: START bottom-left (grid 0,9), then up
 * the left column to JAIL (0,0), along the top to CLUB (9,0), down the
 * right to REST HOUSE (9,9) and back along the bottom. Seen as a diamond
 * that puts START at the bottom tip and JAIL at the left.
 */
object CityBaazarBoardGeometry {
    const val GRID = 10
    private const val LAST = GRID - 1

    /** Grid cell (column, row) of [index]. */
    fun cell(index: Int): Pair<Int, Int> = when {
        index <= JAIL_TILE -> 0 to LAST - index
        index <= CLUB_TILE -> index - JAIL_TILE to 0
        index <= REST_HOUSE_TILE -> LAST to index - CLUB_TILE
        else -> LAST - (index - REST_HOUSE_TILE) to LAST
    }

    /** Unit step from the tile toward the middle of the board (its coloured header side); (0, 0) for corners. */
    fun inward(index: Int): Pair<Int, Int> = when (index) {
        in START_TILE + 1 until JAIL_TILE -> 1 to 0
        in JAIL_TILE + 1 until CLUB_TILE -> 0 to 1
        in CLUB_TILE + 1 until REST_HOUSE_TILE -> -1 to 0
        in REST_HOUSE_TILE + 1 until TILE_COUNT -> 0 to -1
        else -> 0 to 0
    }
}
