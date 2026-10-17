package com.kglabs28.yapnplay.appengine.games.royalludo.utils

import androidx.compose.ui.geometry.Offset
import com.kglabs28.yapnplay.appengine.games.royalludo.utils.HOME_COLUMN_LENGTH
import com.kglabs28.yapnplay.appengine.games.royalludo.utils.PROGRESS_ENTER_HOME
import com.kglabs28.yapnplay.appengine.games.royalludo.utils.PROGRESS_FINISHED
import com.kglabs28.yapnplay.appengine.games.royalludo.utils.PROGRESS_IN_BASE
import com.kglabs28.yapnplay.appengine.games.royalludo.utils.RoyalLudoBoardUtils
import com.kglabs28.yapnplay.appengine.games.royalludo.utils.STAR_SQUARES
import com.kglabs28.yapnplay.appengine.games.royalludo.utils.TRACK_SIZE

/**
 * Where everything sits on the classic 15×15 Ludo board, in grid units
 * (x = column, y = row; (0,0) is the top-left corner, a cell's centre is
 * at +0.5). The canonical board matches the engine: red base top-left,
 * green top-right, yellow bottom-right, blue bottom-left, play goes
 * clockwise from red's start (square 0). Each colour's home lane runs
 * from its own arm into the centre and is painted in that colour.
 * The screen then turns the board in quarter turns so the user's own
 * base is bottom-left ([rotationFor]).
 */
object LudoBoardGeometry {

    const val GRID = 15
    private const val CENTER = GRID / 2f

    /** The 52 shared squares as (column, row), square 0 = red's start. */
    val track: List<Pair<Int, Int>> = buildList {
        for (c in 1..5) add(c to 6)          // red arm, heading right
        for (r in 5 downTo 0) add(6 to r)    // up the left side of green's arm
        add(7 to 0)
        for (r in 0..5) add(8 to r)          // green's start is (8, 1)
        for (c in 9..14) add(c to 6)
        add(14 to 7)
        for (c in 14 downTo 9) add(c to 8)   // yellow's start is (13, 8)
        for (r in 9..14) add(8 to r)
        add(7 to 14)
        for (r in 14 downTo 9) add(6 to r)   // blue's start is (6, 13)
        for (c in 5 downTo 0) add(c to 8)
        add(0 to 7)
        add(0 to 6)
    }.also { check(it.size == TRACK_SIZE) }

    /** Home-lane cells for a colour, from the track into the centre. */
    fun lane(colour: Int): List<Pair<Int, Int>> = (1..HOME_COLUMN_LENGTH).map { step ->
        when (colour) {
            0 -> step to 7
            1 -> 7 to step
            2 -> (GRID - 1 - step) to 7
            else -> 7 to (GRID - 1 - step)
        }
    }

    /** Top-left cell of a colour's 6×6 base. */
    fun baseOrigin(colour: Int): Pair<Int, Int> = when (colour) {
        0 -> 0 to 0
        1 -> 9 to 0
        2 -> 9 to 9
        else -> 0 to 9
    }

    /** Centre of the triangle a colour's finished pieces gather in. */
    fun homeCentre(colour: Int): Offset = when (colour) {
        0 -> Offset(6.55f, CENTER)
        1 -> Offset(CENTER, 6.55f)
        2 -> Offset(8.45f, CENTER)
        else -> Offset(CENTER, 8.45f)
    }

    /** True for the eight safe squares: four starts and four stars. */
    fun isSafe(square: Int): Boolean = RoyalLudoBoardUtils.isSafeSquare(square)

    fun isStar(square: Int): Boolean = square in STAR_SQUARES

    /** The colour whose quarter a shared square belongs to (for tinting). */
    fun ownerOf(square: Int): Int = square / (TRACK_SIZE / 4)

    /** Start square index for a colour. */
    fun startOf(colour: Int): Int = RoyalLudoBoardUtils.startSquareFor(colour)

    /**
     * Canonical grid position of a piece ([progress] as in the engine).
     * Base and finished pieces get their own spot per piece index.
     */
    fun position(colour: Int, progress: Int, piece: Int): Offset = when {
        progress == PROGRESS_IN_BASE -> {
            val (bx, by) = baseOrigin(colour)
            Offset(bx + BASE_SLOTS[piece].x, by + BASE_SLOTS[piece].y)
        }
        progress >= PROGRESS_FINISHED -> homeCentre(colour) + FINISH_OFFSETS[piece].alongTriangle(colour)
        progress > PROGRESS_ENTER_HOME -> lane(colour)[progress - PROGRESS_ENTER_HOME - 1].centre()
        else -> track[(startOf(colour) + progress) % TRACK_SIZE].centre()
    }

    /** Quarter turns (clockwise) that bring [myColour]'s base to the bottom-left. */
    fun rotationFor(myColour: Int): Int = (BOTTOM_LEFT - myColour + 4) % 4

    /** Screen corner of a colour after [rotation]: 0 TL, 1 TR, 2 BR, 3 BL. */
    fun cornerOf(colour: Int, rotation: Int): Int = (colour + rotation) % 4

    /** Turns a canonical grid point clockwise by [quarterTurns] around the board centre. */
    fun rotate(p: Offset, quarterTurns: Int): Offset {
        var q = p
        repeat(((quarterTurns % 4) + 4) % 4) { q = Offset(GRID - q.y, q.x) }
        return q
    }

    private fun Pair<Int, Int>.centre() = Offset(first + 0.5f, second + 0.5f)

    /** Left/right triangles (red, yellow) fan pieces vertically; top/bottom ones horizontally. */
    private fun Offset.alongTriangle(colour: Int): Offset = if (colour % 2 == 0) Offset(y, x) else this

    private const val BOTTOM_LEFT = 3

    /** Four spots inside a 6×6 base. */
    private val BASE_SLOTS = listOf(Offset(2f, 2f), Offset(4f, 2f), Offset(2f, 4f), Offset(4f, 4f))

    /** Finished pieces fan out along their triangle. */
    private val FINISH_OFFSETS = listOf(Offset(-0.45f, 0f), Offset(-0.15f, 0f), Offset(0.15f, 0f), Offset(0.45f, 0f))
}
