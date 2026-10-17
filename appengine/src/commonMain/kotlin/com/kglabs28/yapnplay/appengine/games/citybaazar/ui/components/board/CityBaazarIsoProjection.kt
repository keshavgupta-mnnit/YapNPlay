package com.kglabs28.yapnplay.appengine.games.citybaazar.ui.components.board

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.CardDeck
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.CityBaazarBoardGeometry
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.TILE_COUNT
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

/**
 * 3D view. Board units → screen, as in the design: the board turned 45° into a
 * diamond (START at the bottom tip) and seen from above at an angle.
 * [lift] raises a point straight up off the board (tokens, buildings).
 */
class CityBaazarIsoProjection private constructor(private val origin: Offset, private val scale: Float) {

    fun project(point: Offset, lift: Float = 0f): Offset {
        val (across, depth) = fromCentre(point)
        val f = perspective(depth)
        return Offset(origin.x + across * f * scale, origin.y + (depth * SQUASH - lift) * f * scale)
    }

    /** On-screen size of one board unit near [point]. */
    fun cellSize(point: Offset): Float = scale * perspective(fromCentre(point).second)

    val slabDepth: Float get() = scale * SLAB_DEPTH

    /** Where the two dice stand: side by side, close together, in the middle. */
    fun diceRects(): Pair<Rect, Rect> {
        val c = project(Offset(CENTRE, CENTRE))
        val side = cellSize(Offset(CENTRE, CENTRE)) * DIE_UNITS
        val gap = side * DIE_GAP
        val top = c.y - side * DIE_STAND
        return Rect(Offset(c.x - side - gap / 2, top), Size(side, side)) to Rect(Offset(c.x + gap / 2, top), Size(side, side))
    }

    /** The tile under a tap, if any. */
    fun tileAt(point: Offset): Int? = (0 until TILE_COUNT).minByOrNull { (project(tileCentre(it)) - point).getDistance() }
        ?.takeIf { (project(tileCentre(it)) - point).getDistance() < cellSize(tileCentre(it)) * TAP_REACH }

    /** The Luck or Shagun Chest pile under a tap, if any. */
    fun deckAt(point: Offset): CardDeck? = CardDeck.entries.firstOrNull {
        (project(deckCentre(it)) - point).getDistance() < cellSize(deckCentre(it)) * DECK_REACH
    }

    private fun fromCentre(point: Offset): Pair<Float, Float> {
        val x = point.x - CENTRE
        val y = point.y - CENTRE
        return (x + y) * INV_SQRT2 to (y - x) * INV_SQRT2
    }

    private fun perspective(depth: Float): Float = CAMERA / (CAMERA - depth * PERSPECTIVE)

    companion object {
        private val INV_SQRT2 = 1f / sqrt(2f)
        private const val SQUASH = 0.56f
        private const val CAMERA = 40f
        private const val PERSPECTIVE = 0.5f
        const val CENTRE = CityBaazarBoardGeometry.SIDE / 2f
        /** Board rim beyond the tile ring, in units. */
        const val FRAME = 0.35f
        private const val SLAB_DEPTH = 0.4f
        private const val DIE_UNITS = 2.1f
        private const val DIE_GAP = 0.1f
        private const val DIE_STAND = 0.72f
        private const val TAP_REACH = 0.85f
        private const val DECK_REACH = 1.5f
        /** The Shagun Chest pile sits left of the dice, Luck right (board units from the middle). */
        const val DECK_OFFSET = 2.35f

        fun tileCentre(index: Int): Offset = CityBaazarBoardGeometry.rect(index).let { Offset(it.centreX, it.centreY) }

        fun deckCentre(deck: CardDeck): Offset =
            if (deck == CardDeck.CHEST) Offset(CENTRE - DECK_OFFSET, CENTRE - DECK_OFFSET) else Offset(CENTRE + DECK_OFFSET, CENTRE + DECK_OFFSET)

        /** The largest board that fits [size], centred. */
        fun fit(size: Size): CityBaazarIsoProjection {
            val unit = CityBaazarIsoProjection(Offset.Zero, 1f)
            val lo = -FRAME
            val hi = CityBaazarBoardGeometry.SIDE + FRAME
            val tips = listOf(Offset(lo, lo), Offset(hi, lo), Offset(hi, hi), Offset(lo, hi)).map { unit.project(it) }
            val left = tips.minOf { it.x }
            val right = tips.maxOf { it.x }
            val top = tips.minOf { it.y }
            val bottom = tips.maxOf { it.y } + SLAB_DEPTH
            val scale = min(size.width / (right - left), size.height / (bottom - top))
            val origin = Offset(size.width / 2 - (left + right) / 2 * scale, size.height / 2 - (top + bottom) / 2 * scale)
            return CityBaazarIsoProjection(origin, max(scale, 0f))
        }
    }
}
