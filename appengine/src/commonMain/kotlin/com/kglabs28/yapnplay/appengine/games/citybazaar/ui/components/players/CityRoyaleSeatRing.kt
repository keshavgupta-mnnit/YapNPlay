package com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.players

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.board.CityBazaarIsoProjection
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbPlayerCardUi
import kotlin.math.roundToInt

/**
 * The other players seated round the 3D diamond, clockwise from the user
 * (whose card is in the bottom bar, right): lower left, upper left, top
 * left of the top tip, top right of it, upper right. Each seat takes the
 * spot nearest its place that keeps it off the board, using the board's
 * real edges (the diamond sits [boardDrop] below the area's top, as the
 * board is drawn), keeps above the bottom bar ([barHeight]) and, at the
 * top, below the snackbar's band ([topReserve]). How many players picks
 * the spots, so they spread round the table: 1 upper left, 2 left and
 * right, 3 round the circle; the two top spots only once 4 or 5 need them.
 */
@Composable
fun CityRoyaleSeatRing(
    cards: List<CbPlayerCardUi>,
    boardDrop: Dp,
    barHeight: Dp,
    topReserve: Dp,
    seat: @Composable (CbPlayerCardUi) -> Unit,
) {
    val spots = SPOTS_FOR.getOrElse(cards.size) { SPOTS_FOR.last() }
    Layout(
        content = { cards.take(spots.size).forEach { seat(it) } },
        modifier = Modifier.fillMaxSize(),
    ) { measurables, constraints ->
        val w = constraints.maxWidth.toFloat()
        val h = constraints.maxHeight.toFloat()
        val placeables = measurables.map { it.measure(Constraints(maxWidth = constraints.maxWidth, maxHeight = constraints.maxHeight)) }
        val drop = boardDrop.toPx()
        val (left, top, right, bottom) = CityBazaarIsoProjection.tips(Size(w, h - drop)).map { it + Offset(0f, drop) }
        val margin = Spacing.Sm.toPx()
        val gap = Spacing.Sm.toPx()
        val barTop = h - barHeight.toPx() - gap
        val topY = topReserve.toPx() + gap

        /** The board's left edge at height [y] (none above or below it). */
        fun boardLeft(y: Float): Float = when {
            y < top.y || y > bottom.y -> w
            y <= left.y -> lerp(top.x, left.x, (y - top.y) / (left.y - top.y))
            else -> lerp(left.x, bottom.x, (y - left.y) / (bottom.y - left.y))
        }
        fun boardRight(y: Float): Float = when {
            y < top.y || y > bottom.y -> 0f
            y <= right.y -> lerp(top.x, right.x, (y - top.y) / (right.y - top.y))
            else -> lerp(right.x, bottom.x, (y - right.y) / (bottom.y - right.y))
        }
        /** The height on the upper-left edge where it is [x] across (solves boardLeft). */
        fun upperAt(x: Float, tipX: Float, sideX: Float, sideY: Float): Float =
            top.y + (sideY - top.y) * ((tipX - x) / (tipX - sideX)).coerceIn(0f, 1f)
        fun lowerAt(x: Float, sideX: Float, sideY: Float): Float =
            sideY + (bottom.y - sideY) * ((x - sideX) / (bottom.x - sideX)).coerceIn(0f, 1f)

        val positions = spots.zip(placeables).map { (spot, p) ->
            val cw = p.width.toFloat()
            val ch = p.height.toFloat()
            when (spot) {
                Spot.LOWER_LEFT -> {
                    // As high as the lower-left edge allows, above the bar.
                    val y = lowerAt(margin + cw + gap, left.x, left.y).coerceAtMost(barTop - ch).coerceAtLeast(left.y + gap / 2)
                    Offset(margin, y)
                }
                Spot.UPPER_LEFT -> {
                    // As low as the upper-left edge allows, above the left tip.
                    val y = (upperAt(margin + cw + gap, top.x, left.x, left.y) - ch).coerceAtMost(left.y - gap / 2 - ch).coerceAtLeast(0f)
                    Offset(margin, y)
                }
                Spot.TOP_LEFT -> {
                    // Under the snackbar's band, just left of the board's upper-left edge.
                    val x = (minOf(boardLeft(topY + ch), top.x) - gap - cw).coerceAtLeast(margin)
                    Offset(x, topY)
                }
                Spot.TOP_RIGHT -> {
                    val x = (maxOf(boardRight(topY + ch), top.x) + gap).coerceAtMost(w - margin - cw)
                    Offset(x, topY)
                }
                Spot.UPPER_RIGHT -> {
                    val y = (upperAt(w - margin - cw - gap, top.x, right.x, right.y) - ch).coerceAtMost(right.y - gap / 2 - ch).coerceAtLeast(0f)
                    Offset(w - margin - cw, y)
                }
            }
        }
        // Top-corner seats never sit on the upper seats beside them.
        val fixed = positions.mapIndexed { i, at ->
            val spot = spots[i]
            val p = placeables[i]
            val beside = when (spot) {
                Spot.TOP_LEFT -> spots.indexOf(Spot.UPPER_LEFT)
                Spot.TOP_RIGHT -> spots.indexOf(Spot.UPPER_RIGHT)
                else -> -1
            }
            if (beside < 0 || positions[beside].y >= at.y + p.height + gap) {
                at
            } else if (spot == Spot.TOP_LEFT) {
                at.copy(x = maxOf(at.x, positions[beside].x + placeables[beside].width + gap))
            } else {
                at.copy(x = minOf(at.x, positions[beside].x - p.width - gap))
            }
        }
        layout(constraints.maxWidth, constraints.maxHeight) {
            placeables.forEachIndexed { i, p -> p.place(fixed[i].x.roundToInt(), fixed[i].y.roundToInt()) }
        }
    }
}

private fun lerp(a: Float, b: Float, t: Float): Float = a + (b - a) * t

private enum class Spot { LOWER_LEFT, UPPER_LEFT, TOP_LEFT, TOP_RIGHT, UPPER_RIGHT }

/** The spots for 0…5 other players, clockwise from the user. */
private val SPOTS_FOR = listOf(
    emptyList(),
    listOf(Spot.UPPER_LEFT),
    listOf(Spot.UPPER_LEFT, Spot.UPPER_RIGHT),
    listOf(Spot.LOWER_LEFT, Spot.UPPER_LEFT, Spot.UPPER_RIGHT),
    listOf(Spot.LOWER_LEFT, Spot.UPPER_LEFT, Spot.TOP_RIGHT, Spot.UPPER_RIGHT),
    listOf(Spot.LOWER_LEFT, Spot.UPPER_LEFT, Spot.TOP_LEFT, Spot.TOP_RIGHT, Spot.UPPER_RIGHT),
)
