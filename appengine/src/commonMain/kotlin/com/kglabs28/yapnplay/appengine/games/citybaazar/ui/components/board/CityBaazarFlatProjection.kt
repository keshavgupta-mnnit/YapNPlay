package com.kglabs28.yapnplay.appengine.games.citybaazar.ui.components.board

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.CardDeck
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.CityBaazarBoardGeometry
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.TILE_COUNT
import kotlin.math.max
import kotlin.math.min

/**
 * Board units → screen for the flat, square board seen straight from
 * above: START bottom-left. [lift] raises a point straight up the screen
 * (a token's hop).
 */
class CityBaazarFlatProjection private constructor(private val origin: Offset, private val scale: Float) {

    fun project(point: Offset, lift: Float = 0f): Offset = Offset(origin.x + point.x * scale, origin.y + (point.y - lift) * scale)

    /** On-screen size of one board unit. */
    fun cellSize(@Suppress("UNUSED_PARAMETER") point: Offset): Float = scale

    val unit: Float get() = scale

    /** Where the two dice sit: big, side by side, nearly touching, in the middle. */
    fun diceRects(): Pair<Rect, Rect> {
        val c = project(Offset(CENTRE, CENTRE))
        val side = scale * DIE_UNITS
        val gap = side * DIE_GAP
        val top = c.y - side / 2
        return Rect(Offset(c.x - side - gap / 2, top), Size(side, side)) to Rect(Offset(c.x + gap / 2, top), Size(side, side))
    }

    /** The tile under a tap, if any. */
    fun tileAt(point: Offset): Int? = (0 until TILE_COUNT).firstOrNull { index ->
        val r = CityBaazarBoardGeometry.rect(index)
        val a = project(Offset(r.x0, r.y0))
        val b = project(Offset(r.x1, r.y1))
        point.x in a.x..b.x && point.y in a.y..b.y
    }

    /** The Luck or Shagun Chest pile under a tap, if any. */
    fun deckAt(point: Offset): CardDeck? = CardDeck.entries.firstOrNull {
        val c = project(deckCentre(it))
        val half = scale * DECK_HALF
        point.x in c.x - half..c.x + half && point.y in c.y - half..c.y + half
    }

    companion object {
        const val CENTRE = CityBaazarBoardGeometry.SIDE / 2f
        /** Board rim beyond the tile ring, in units. */
        const val FRAME = 0.3f
        const val DECK_HALF = 1.15f
        /** Shagun Chest top-left of the dice, Luck bottom-right (units from the middle, both axes). */
        private const val DECK_OFFSET = 2.6f
        private const val DIE_UNITS = 2.2f
        private const val DIE_GAP = 0.06f

        fun tileCentre(index: Int): Offset = CityBaazarBoardGeometry.rect(index).let { Offset(it.centreX, it.centreY) }

        fun deckCentre(deck: CardDeck): Offset =
            if (deck == CardDeck.CHEST) Offset(CENTRE - DECK_OFFSET, CENTRE - DECK_OFFSET) else Offset(CENTRE + DECK_OFFSET, CENTRE + DECK_OFFSET)

        /** The largest square board that fits [size], centred. */
        fun fit(size: Size): CityBaazarFlatProjection {
            val total = CityBaazarBoardGeometry.SIDE + 2 * FRAME
            val scale = max(min(size.width, size.height) / total, 0f)
            val side = CityBaazarBoardGeometry.SIDE * scale
            return CityBaazarFlatProjection(Offset((size.width - side) / 2, (size.height - side) / 2), scale)
        }
    }
}
