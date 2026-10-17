package com.kglabs28.yapnplay.appengine.games.citybaazar.utils

/**
 * The tile ring on the board, in board units. Edge tiles are 1 wide and
 * [DEPTH] deep (deeper than wide, so names fit and the middle is smaller);
 * corners are [DEPTH] square. START is bottom-left, then up the left side
 * to JAIL, along the top to CLUB, down the right to REST HOUSE and back
 * along the bottom. Seen as a diamond: START at the bottom tip, JAIL left.
 */
object CityBaazarBoardGeometry {
    const val DEPTH = 1.7f
    private const val EDGE_TILES = 8
    const val SIDE = 2 * DEPTH + EDGE_TILES

    fun rect(index: Int): TileRect {
        val far = SIDE - DEPTH
        return when {
            index == START_TILE -> TileRect(0f, far, DEPTH, SIDE)
            index < JAIL_TILE -> (far - (index - 1)).let { y1 -> TileRect(0f, y1 - 1, DEPTH, y1) }
            index == JAIL_TILE -> TileRect(0f, 0f, DEPTH, DEPTH)
            index < CLUB_TILE -> (DEPTH + (index - JAIL_TILE - 1)).let { x0 -> TileRect(x0, 0f, x0 + 1, DEPTH) }
            index == CLUB_TILE -> TileRect(far, 0f, SIDE, DEPTH)
            index < REST_HOUSE_TILE -> (DEPTH + (index - CLUB_TILE - 1)).let { y0 -> TileRect(far, y0, SIDE, y0 + 1) }
            index == REST_HOUSE_TILE -> TileRect(far, far, SIDE, SIDE)
            else -> (far - (index - REST_HOUSE_TILE - 1)).let { x1 -> TileRect(x1 - 1, far, x1, SIDE) }
        }
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
