package com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.board

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.IntOffset
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbTileUi
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbTokenUi
import com.kglabs28.yapnplay.appengine.games.citybazaar.utils.CardDeck
import kotlin.math.roundToInt

/**
 * The City Bazaar board, as large as fits, flat (2D) or as a diamond (3D): tiles, buildings and owners
 * (painter), the players' tokens hopping tile to tile, and the two dice
 * standing in the middle ([dice] gets one slot modifier per die). A tap on
 * a tile asks for its card; [highlight] tiles get a plain border while their cards are open. No glow
 * anywhere on the board (only the dice glow).
 */
@Composable
fun CityBazaarBoardView(
    tiles: List<CbTileUi>,
    tokens: List<CbTokenUi>,
    onTileClick: (Int) -> Unit,
    onDeckClick: (CardDeck) -> Unit,
    modifier: Modifier = Modifier,
    highlight: Set<Int> = emptySet(),
    /** True: the diamond 3D board; false: the flat square board. */
    threeD: Boolean = false,
    dice: @Composable (left: Modifier, right: Modifier) -> Unit = { _, _ -> },
) {
    val colours = cityBazaarBoardColours()
    val measurer = rememberTextMeasurer()
    val icons = rememberCbTileIcons()
    val spots = tokens.map { token ->
        key(token.seat) { animateTokenSpot(token.seat, token.tile, CityBazaarTokenLayout.target(token, tokens)) to (colours.players[token.colour] ?: colours.text) }
    }
    BoxWithConstraints(modifier = modifier) {
        val size = Size(constraints.maxWidth.toFloat(), constraints.maxHeight.toFloat())
        val (left, right) = if (threeD) {
            val projection = remember(size) { CityBazaarIsoProjection.fit(size) }
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(projection) { detectTapGestures { point -> projection.deckAt(point)?.let(onDeckClick) ?: projection.tileAt(point)?.let(onTileClick) } },
            ) {
                drawCityBazaarIsoBoard(projection, colours, tiles, measurer, highlight, icons)
                drawCityBazaarIsoTokens(projection, spots)
            }
            projection.diceRects()
        } else {
            val projection = remember(size) { CityBazaarFlatProjection.fit(size) }
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(projection) { detectTapGestures { point -> projection.deckAt(point)?.let(onDeckClick) ?: projection.tileAt(point)?.let(onTileClick) } },
            ) {
                drawCityBazaarFlatBoard(projection, colours, tiles, measurer, highlight, icons)
                drawCityBazaarFlatTokens(projection, spots)
            }
            projection.diceRects()
        }
        with(LocalDensity.current) {
            dice(
                Modifier.offset { IntOffset(left.left.roundToInt(), left.top.roundToInt()) }.size(left.width.toDp()),
                Modifier.offset { IntOffset(right.left.roundToInt(), right.top.roundToInt()) }.size(right.width.toDp()),
            )
        }
    }
}

