package com.kglabs28.yapnplay.appengine.ui.components.table

/**
 * The seating for the card table (MONO and Complete Chaos share one table
 * of one size). The user always sits bottom left; everyone else sits
 * round the U — left column (drawn bottom-up), top row, right column —
 * clockwise from the user. Seats per side, for 1…7 other players:
 *  - 1: top                - 5: left 1, top 2, right 2
 *  - 2: left, right        - 6: left 2, top 2, right 2
 *  - 3: left, top, right   - 7: left 2, top 3, right 2
 *  - 4: left 1, top 2, right 1
 * A watcher has no seat, so all 1…8 players spread evenly round the U
 * (8: left 3, top 2, right 3) and the bottom is left free.
 */
object TableSeatPlan {
    /** [opponents] in turn order split into (left, top, right); [watching]: everyone, spread evenly. */
    fun <T> zones(opponents: List<T>, watching: Boolean = false): Triple<List<T>, List<T>, List<T>> {
        val sides = if (watching) WATCHING_SIDES else SIDES
        val (left, top, right) = sides.getOrElse(opponents.size) { sides.last() }
        return Triple(
            opponents.take(left),
            opponents.drop(left).take(top),
            opponents.drop(left + top).take(right),
        )
    }

    /**
     * How big every opponent's seat is drawn (avatar, pill, name, speaker,
     * fan together), in every game, for [opponents] of them: the table stays
     * the same size, so seats grow when few play and shrink as it fills.
     */
    fun scale(opponents: Int): Float = SCALES.getOrElse(opponents) { SCALES.last() }

    /** (left, top, right) seats for 0…7 other players. */
    private val SIDES = listOf(
        Triple(0, 0, 0),
        Triple(0, 1, 0),
        Triple(1, 0, 1),
        Triple(1, 1, 1),
        Triple(1, 2, 1),
        Triple(1, 2, 2),
        Triple(2, 2, 2),
        Triple(2, 3, 2),
    )

    /** (left, top, right) seats for 0…8 players when watching. */
    private val WATCHING_SIDES = listOf(
        Triple(0, 0, 0),
        Triple(0, 1, 0),
        Triple(1, 0, 1),
        Triple(1, 1, 1),
        Triple(1, 2, 1),
        Triple(2, 1, 2),
        Triple(2, 2, 2),
        Triple(2, 3, 2),
        Triple(3, 2, 3),
    )

    /** Seat size for 0…7 other players: 1–2 roomy, 3 a little less, 4–5 smaller, 6–7 smallest. */
    private val SCALES = listOf(1.2f, 1.2f, 1.2f, 1.1f, 0.85f, 0.85f, 0.72f, 0.72f)
}
