package com.kglabs28.yapnplay.appengine.games.citybaazar.ui.components.board

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.CityBaazarBoardGeometry
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.TILE_COUNT
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

/**
 * Board grid → screen, as in the design: the 10×10 ring turned 45° into a
 * diamond (START at the bottom tip) and seen from above at an angle.
 * [lift] raises a point straight up off the board (tokens, buildings).
 */
class CityBaazarBoardProjection private constructor(private val origin: Offset, private val scale: Float) {

    fun project(grid: Offset, lift: Float = 0f): Offset {
        val (across, depth) = fromCentre(grid)
        val f = perspective(depth)
        return Offset(origin.x + across * f * scale, origin.y + (depth * SQUASH - lift) * f * scale)
    }

    /** On-screen size of one cell near [grid]. */
    fun cellSize(grid: Offset): Float = scale * perspective(fromCentre(grid).second)

    val slabDepth: Float get() = scale * SLAB_DEPTH

    /** Where the two dice stand, side by side in the middle. */
    fun diceRects(): Pair<Rect, Rect> {
        val c = project(Offset(CENTRE, CENTRE))
        val side = cellSize(Offset(CENTRE, CENTRE)) * DIE_CELLS
        val gap = side * DIE_GAP
        val top = c.y - side * DIE_STAND
        return Rect(Offset(c.x - side - gap / 2, top), Size(side, side)) to Rect(Offset(c.x + gap / 2, top), Size(side, side))
    }

    /** The tile under a tap, if any. */
    fun tileAt(point: Offset): Int? = (0 until TILE_COUNT).minByOrNull { (project(tileCentre(it)) - point).getDistance() }
        ?.takeIf { (project(tileCentre(it)) - point).getDistance() < cellSize(tileCentre(it)) * TAP_REACH }

    private fun fromCentre(grid: Offset): Pair<Float, Float> {
        val x = grid.x - CENTRE
        val y = grid.y - CENTRE
        return (x + y) * INV_SQRT2 to (y - x) * INV_SQRT2
    }

    private fun perspective(depth: Float): Float = CAMERA / (CAMERA - depth * PERSPECTIVE)

    companion object {
        private val INV_SQRT2 = 1f / sqrt(2f)
        private const val SQUASH = 0.56f
        private const val CAMERA = 40f
        private const val PERSPECTIVE = 0.5f
        const val CENTRE = CityBaazarBoardGeometry.GRID / 2f
        /** Board rim beyond the tile ring, in cells. */
        const val FRAME = 0.35f
        private const val SLAB_DEPTH = 0.4f
        private const val DIE_CELLS = 1.35f
        private const val DIE_GAP = 0.25f
        private const val DIE_STAND = 0.7f
        private const val TAP_REACH = 0.6f
        private const val HALF = 0.5f

        fun tileCentre(index: Int): Offset {
            val (c, r) = CityBaazarBoardGeometry.cell(index)
            return Offset(c + HALF, r + HALF)
        }

        /** The largest board that fits [size], centred. */
        fun fit(size: Size): CityBaazarBoardProjection {
            val unit = CityBaazarBoardProjection(Offset.Zero, 1f)
            val lo = -FRAME
            val hi = CityBaazarBoardGeometry.GRID + FRAME
            val tips = listOf(Offset(lo, lo), Offset(hi, lo), Offset(hi, hi), Offset(lo, hi)).map { unit.project(it) }
            val left = tips.minOf { it.x }
            val right = tips.maxOf { it.x }
            val top = tips.minOf { it.y }
            val bottom = tips.maxOf { it.y } + SLAB_DEPTH
            val scale = min(size.width / (right - left), size.height / (bottom - top))
            val origin = Offset(size.width / 2 - (left + right) / 2 * scale, size.height / 2 - (top + bottom) / 2 * scale)
            return CityBaazarBoardProjection(origin, max(scale, 0f))
        }
    }
}
