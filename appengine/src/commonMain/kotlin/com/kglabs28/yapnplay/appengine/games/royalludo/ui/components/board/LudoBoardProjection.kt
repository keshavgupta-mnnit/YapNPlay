package com.kglabs28.yapnplay.appengine.games.royalludo.ui.components.board

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import com.kglabs28.yapnplay.appengine.games.royalludo.utils.LudoBoardGeometry
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

/**
 * Board grid → screen, as in the design: the board turned 45° into a
 * diamond and seen from above at an angle (flattened vertically). The
 * perspective is kept very slight so squares on the far side stay about
 * as big as near ones and are easy to tap. The grid is first turned
 * [rotation] quarter turns so the user's base (bottom-left corner)
 * becomes the bottom tip; the others sit left, top and right — the same
 * corners as their cards.
 * [lift] raises a point straight up off the board (pieces, hops).
 */
class LudoBoardProjection private constructor(
    private val origin: Offset,
    private val scale: Float,
    val rotation: Int,
) {
    /** A canonical grid point (the board as the engine sees it) on screen. */
    fun project(canonical: Offset, lift: Float = 0f): Offset =
        projectView(LudoBoardGeometry.rotate(canonical, rotation), lift)

    /** A grid point already turned to the user's view, on screen. */
    fun projectView(view: Offset, lift: Float = 0f): Offset {
        val (across, depth) = fromCentre(view)
        val f = perspective(depth)
        return Offset(origin.x + across * f * scale, origin.y + (depth * SQUASH - lift) * f * scale)
    }

    /** On-screen size of one cell near [canonical] (perspective aware). */
    fun cellSize(canonical: Offset): Float =
        scale * perspective(fromCentre(LudoBoardGeometry.rotate(canonical, rotation)).second)

    /** The board's four tips on screen: left, top, right, bottom (the bases sit at them). */
    fun tips(): List<Offset> {
        val lo = -FRAME
        val hi = LudoBoardGeometry.GRID + FRAME
        return listOf(Offset(lo, lo), Offset(hi, lo), Offset(hi, hi), Offset(lo, hi)).map { projectView(it) }
    }

    /** Thickness of the board's front edges, in pixels. */
    val slabDepth: Float get() = scale * SLAB_DEPTH

    /** Where the dice sits: on the centre plate, standing up. */
    fun diceRect(): Rect {
        val c = projectView(Offset(CENTRE, CENTRE))
        val side = cellSize(Offset(CENTRE, CENTRE)) * DICE_CELLS
        return Rect(Offset(c.x - side / 2, c.y - side * DICE_STAND), Size(side, side))
    }

    /** (across, depth): the grid turned 45°, depth growing toward the viewer. */
    private fun fromCentre(view: Offset): Pair<Float, Float> {
        val x = view.x - CENTRE
        val y = view.y - CENTRE
        return (x + y) * INV_SQRT2 to (y - x) * INV_SQRT2
    }

    private fun perspective(depth: Float): Float = CAMERA / (CAMERA - depth * PERSPECTIVE)

    companion object {
        private val INV_SQRT2 = 1f / sqrt(2f)
        /** Vertical flattening (board seen at an angle). */
        private const val SQUASH = 0.54f
        private const val CAMERA = 60f
        /** Very slight, so far squares stay nearly as big as near ones. */
        private const val PERSPECTIVE = 0.5f
        private const val CENTRE = LudoBoardGeometry.GRID / 2f
        /** Board rim beyond the 15×15 grid, in cells. */
        const val FRAME = 0.4f
        private const val SLAB_DEPTH = 0.45f
        private const val DICE_CELLS = 3.6f
        private const val DICE_STAND = 0.66f

        /** The largest board that fits [size], centred. */
        fun fit(size: Size, rotation: Int): LudoBoardProjection {
            val unit = LudoBoardProjection(Offset.Zero, 1f, rotation)
            val lo = -FRAME
            val hi = LudoBoardGeometry.GRID + FRAME
            val tips = listOf(Offset(lo, lo), Offset(hi, lo), Offset(hi, hi), Offset(lo, hi)).map { unit.projectView(it) }
            val left = tips.minOf { it.x }
            val right = tips.maxOf { it.x }
            val top = tips.minOf { it.y }
            val bottom = tips.maxOf { it.y } + SLAB_DEPTH
            val scale = min(size.width / (right - left), size.height / (bottom - top))
            val origin = Offset(
                size.width / 2 - (left + right) / 2 * scale,
                size.height / 2 - (top + bottom) / 2 * scale,
            )
            return LudoBoardProjection(origin, max(scale, 0f), rotation)
        }
    }
}
