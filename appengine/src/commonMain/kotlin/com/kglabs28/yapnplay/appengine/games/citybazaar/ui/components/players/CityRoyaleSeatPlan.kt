package com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.players

import androidx.compose.ui.geometry.Offset

/**
 * Where the other players sit round the 3D City Royale board, for each
 * player count (the user is always at the bottom right, in the bar; a
 * watcher has no seat, so everyone spreads evenly round the board). The
 * seats go round the board clockwise in turn order, and keep clear of
 * three things: the bottom bar, the snackbar's band at the top, and the
 * board itself (its four [tips] in px: left, top, right, bottom). A side
 * seat sits just above the board's corner (never on it), clear of the
 * board's upper edge and below any top seat on the same side.
 *
 * Seats for 2…6 players, playing:
 *  - 2: top left (across from the user)
 *  - 3: left, right
 *  - 4: left, top left, right
 *  - 5: left, top left, top right, right
 *  - 6: lower left, upper left, top left, top right, right
 * Watching (everyone seated):
 *  - 2: left, right
 *  - 3: left, top left, right
 *  - 4: left, top left, top right, right
 *  - 5: lower left, upper left, top left, top right, right
 *  - 6: lower left, upper left, top left, top right, right, lower right
 */
class CityRoyaleSeatPlan(
    private val width: Float,
    private val tips: List<Offset>,
    /** The lowest a seat may reach (the bar's top, less a gap) and the highest a top seat may start (under the snackbar). */
    private val bottomLimit: Float,
    private val topLimit: Float,
    private val margin: Float,
    private val gap: Float,
) {
    private val left get() = tips[0]
    private val top get() = tips[1]
    private val right get() = tips[2]
    private val bottom get() = tips[3]

    enum class Spot { LOWER_LEFT, LEFT, UPPER_LEFT, TOP_LEFT, TOP_RIGHT, RIGHT, LOWER_RIGHT }

    /** The top-left corner of a [cardWidth] × [cardHeight] card at [spot], among the layout's [spots]. */
    fun place(spot: Spot, spots: List<Spot>, cardWidth: Float, cardHeight: Float): Offset {
        // A side seat stays below the top seat on its side, when there is one.
        val underTop = topLimit + cardHeight + gap
        val leftFloor = if (Spot.TOP_LEFT in spots) underTop else topLimit
        val rightFloor = if (Spot.TOP_RIGHT in spots) underTop else topLimit
        return when (spot) {
            // Just above the side tip's line, clear of the board.
            Spot.LEFT, Spot.UPPER_LEFT -> Offset(margin, maxOf(clearAbove(left.y - gap - cardHeight, margin, cardWidth, cardHeight), leftFloor).coerceAtMost(bottomLimit - cardHeight))
            Spot.RIGHT -> (width - margin - cardWidth).let { x ->
                Offset(x, maxOf(clearAbove(right.y - gap - cardHeight, x, cardWidth, cardHeight), rightFloor).coerceAtMost(bottomLimit - cardHeight))
            }
            // The lower of two on the left: just below the tip's line, clear of the board.
            Spot.LOWER_LEFT -> Offset(margin, maxOf(left.y + gap / 2, boardLowerAt(margin + cardWidth) + gap).coerceAtMost(bottomLimit - cardHeight))
            Spot.LOWER_RIGHT -> (width - margin - cardWidth).let { x ->
                Offset(x, maxOf(right.y + gap / 2, boardLowerAt(x) + gap).coerceAtMost(bottomLimit - cardHeight))
            }
            // At the top, under the snackbar's band, just off the board's upper edges.
            Spot.TOP_LEFT -> Offset((minOf(boardLeftAt(topLimit + cardHeight), top.x) - gap - cardWidth).coerceAtLeast(margin), topLimit)
            Spot.TOP_RIGHT -> Offset((maxOf(boardRightAt(topLimit + cardHeight), top.x) + gap).coerceAtMost(width - margin - cardWidth), topLimit)
        }
    }

    /**
     * The highest a [cardWidth] × [cardHeight] card at [x] may start from
     * [wanted]: no lower than its bottom clearing the board's upper edge
     * under it, and kept between the snackbar's band and the bar.
     */
    private fun clearAbove(wanted: Float, x: Float, cardWidth: Float, cardHeight: Float): Float {
        val edge = minOf(boardUpperAt(x), boardUpperAt(x + cardWidth))
        // A very short screen (a split window, a tiny tablet pane) may not fit a card at all: then it sits at the top, never throws.
        return minOf(wanted, edge - gap - cardHeight).coerceIn(topLimit, maxOf(topLimit, bottomLimit - cardHeight))
    }

    /** The board's upper edge (left tip → top → right tip) at [x]; no board there: below everything. */
    private fun boardUpperAt(x: Float): Float = when {
        x <= left.x || x >= right.x -> Float.MAX_VALUE
        x <= top.x -> lerp(left.y, top.y, (x - left.x) / (top.x - left.x))
        else -> lerp(top.y, right.y, (x - top.x) / (right.x - top.x))
    }

    /** The board's lower edge (left tip → bottom → right tip) at [x]; no board there: above everything. */
    private fun boardLowerAt(x: Float): Float = when {
        x <= left.x || x >= right.x -> -Float.MAX_VALUE
        x <= bottom.x -> lerp(left.y, bottom.y, (x - left.x) / (bottom.x - left.x))
        else -> lerp(bottom.y, right.y, (x - bottom.x) / (right.x - bottom.x))
    }

    /** The board's left edge at height [y] (no board above or below it). */
    private fun boardLeftAt(y: Float): Float = when {
        y < top.y || y > bottom.y -> width
        y <= left.y -> lerp(top.x, left.x, (y - top.y) / (left.y - top.y))
        else -> lerp(left.x, bottom.x, (y - left.y) / (bottom.y - left.y))
    }

    private fun boardRightAt(y: Float): Float = when {
        y < top.y || y > bottom.y -> 0f
        y <= right.y -> lerp(top.x, right.x, (y - top.y) / (right.y - top.y))
        else -> lerp(right.x, bottom.x, (y - right.y) / (bottom.y - right.y))
    }

    private fun lerp(a: Float, b: Float, t: Float): Float = a + (b - a) * t

    companion object {
        /** The seats for [others] players (0…5 besides the user, or 0…6 when [watching]), clockwise in turn order. */
        fun spotsFor(others: Int, watching: Boolean): List<Spot> =
            if (watching) WATCHING.getOrElse(others) { WATCHING.last() } else SEATS.getOrElse(others) { SEATS.last() }

        private val SEATS = listOf(
            emptyList(),
            listOf(Spot.TOP_LEFT),
            listOf(Spot.LEFT, Spot.RIGHT),
            listOf(Spot.LEFT, Spot.TOP_LEFT, Spot.RIGHT),
            listOf(Spot.LEFT, Spot.TOP_LEFT, Spot.TOP_RIGHT, Spot.RIGHT),
            listOf(Spot.LOWER_LEFT, Spot.UPPER_LEFT, Spot.TOP_LEFT, Spot.TOP_RIGHT, Spot.RIGHT),
        )

        private val WATCHING = listOf(
            emptyList(),
            listOf(Spot.LEFT),
            listOf(Spot.LEFT, Spot.RIGHT),
            listOf(Spot.LEFT, Spot.TOP_LEFT, Spot.RIGHT),
            listOf(Spot.LEFT, Spot.TOP_LEFT, Spot.TOP_RIGHT, Spot.RIGHT),
            listOf(Spot.LOWER_LEFT, Spot.UPPER_LEFT, Spot.TOP_LEFT, Spot.TOP_RIGHT, Spot.RIGHT),
            listOf(Spot.LOWER_LEFT, Spot.UPPER_LEFT, Spot.TOP_LEFT, Spot.TOP_RIGHT, Spot.RIGHT, Spot.LOWER_RIGHT),
        )
    }
}
