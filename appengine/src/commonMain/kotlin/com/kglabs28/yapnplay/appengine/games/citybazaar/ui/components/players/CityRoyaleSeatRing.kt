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
 * The other players seated round the 3D board, each card at its seat in
 * [CityRoyaleSeatPlan] for this many players. The board sits [boardDrop]
 * below the area's top (as it is drawn); seats keep above the bottom bar
 * ([barHeight]) and below the snackbar's band ([topReserve]). A watcher
 * ([watching]) has no seat of their own, so everyone spreads evenly.
 */
@Composable
fun CityRoyaleSeatRing(
    cards: List<CbPlayerCardUi>,
    boardDrop: Dp,
    barHeight: Dp,
    topReserve: Dp,
    watching: Boolean,
    modifier: Modifier = Modifier,
    seat: @Composable (CbPlayerCardUi) -> Unit,
) {
    val spots = CityRoyaleSeatPlan.spotsFor(cards.size, watching)
    Layout(
        content = { cards.take(spots.size).forEach { seat(it) } },
        modifier = modifier.fillMaxSize(),
    ) { measurables, constraints ->
        val w = constraints.maxWidth.toFloat()
        val h = constraints.maxHeight.toFloat()
        val placeables = measurables.map { it.measure(Constraints(maxWidth = constraints.maxWidth, maxHeight = constraints.maxHeight)) }
        val drop = boardDrop.toPx()
        val gap = Spacing.Sm.toPx()
        val plan = CityRoyaleSeatPlan(
            width = w,
            tips = CityBazaarIsoProjection.tips(Size(w, h - drop)).map { it + Offset(0f, drop) },
            bottomLimit = h - barHeight.toPx() - gap,
            topLimit = topReserve.toPx() + gap,
            margin = Spacing.Sm.toPx(),
            gap = gap,
        )
        layout(constraints.maxWidth, constraints.maxHeight) {
            spots.zip(placeables).forEach { (spot, p) ->
                val at = plan.place(spot, spots, p.width.toFloat(), p.height.toFloat())
                p.place(at.x.roundToInt(), at.y.roundToInt())
            }
        }
    }
}
