package com.kglabs28.yapnplay.appengine.games.citybaazar.ui.components.board

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.IntOffset
import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.games.citybaazar.ui.screens.match.CbTileUi
import com.kglabs28.yapnplay.appengine.games.citybaazar.ui.screens.match.CbTokenUi
import kotlin.math.roundToInt

/**
 * The City Baazar board, as large as fits: tiles, buildings and owners
 * (painter), the players' tokens hopping tile to tile, and the two dice
 * standing in the middle ([dice] gets one slot modifier per die). A tap on
 * a tile asks for its rate card.
 */
@Composable
fun CityBaazarBoardView(
    tiles: List<CbTileUi>,
    tokens: List<CbTokenUi>,
    onTileClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    dice: @Composable (left: Modifier, right: Modifier) -> Unit = { _, _ -> },
) {
    val colours = cityBaazarBoardColours()
    val measurer = rememberTextMeasurer()
    val glow by rememberInfiniteTransition().animateFloat(
        initialValue = GLOW_LOW,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(AppConstants.LUDO_NEON_BREATH_MS), RepeatMode.Reverse),
    )
    val spots = tokens.map { token ->
        key(token.seat) { animateTokenSpot(token.seat, CityBaazarTokenLayout.target(token, tokens)) to (colours.players[token.colour] ?: colours.text) }
    }
    BoxWithConstraints(modifier = modifier) {
        val size = Size(constraints.maxWidth.toFloat(), constraints.maxHeight.toFloat())
        val projection = remember(size) { CityBaazarBoardProjection.fit(size) }
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(projection) { detectTapGestures { point -> projection.tileAt(point)?.let(onTileClick) } },
        ) {
            drawCityBaazarBoard(projection, colours, tiles, measurer, glow)
            drawCityBaazarTokens(projection, spots)
        }
        val (left, right) = projection.diceRects()
        with(LocalDensity.current) {
            dice(
                Modifier.offset { IntOffset(left.left.roundToInt(), left.top.roundToInt()) }.size(left.width.toDp()),
                Modifier.offset { IntOffset(right.left.roundToInt(), right.top.roundToInt()) }.size(right.width.toDp()),
            )
        }
    }
}

private const val GLOW_LOW = 0.45f
