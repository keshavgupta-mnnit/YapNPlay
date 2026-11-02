package com.kglabs28.yapnplay.appengine.games.royalludo.ui.components.players

import com.kglabs28.yapnplay.appengine.core.designsystem.AdaptiveScale
import com.kglabs28.yapnplay.appengine.ui.components.table.seatButtonsRoom
import com.kglabs28.yapnplay.appengine.ui.components.table.monoScaled
import com.kglabs28.yapnplay.appengine.ui.components.table.TableSeatPlan
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.games.royalludo.ui.components.board.LudoBoardProjection
import com.kglabs28.yapnplay.appengine.games.royalludo.ui.screens.match.BoardCorner
import com.kglabs28.yapnplay.appengine.games.royalludo.ui.screens.match.LudoPlayerCardUi
import com.kglabs28.yapnplay.appengine.ui.components.table.TablePlayerCard
import kotlin.math.roundToInt

/**
 * Each player's card right by their own base, as marked on the design.
 * The board is a diamond (the same projection the board draws with, over
 * [areaSize]): the grid's bottom-left base is its bottom tip, then left,
 * top and right ([BoardCorner] BOTTOM_LEFT, TOP_LEFT, TOP_RIGHT,
 * BOTTOM_RIGHT). One card on each side of the diamond: the top base's on
 * the upper-left edge, the right base's on the upper-right, the bottom
 * base's on the lower-right and the left base's on the lower-left. A card
 * on a left edge runs leftwards from its spot, one on a right edge
 * rightwards, so none lies on the board. Positions are fractions of the
 * board's size, so they hold on any screen; a card never leaves the area.
 * Every card is [cardWidth] wide. [own] draws the user's card (with its
 * switches).
 */
@Composable
fun BoxScope.LudoCornerCards(
    cards: List<LudoPlayerCardUi>,
    cardWidth: Dp,
    areaSize: IntSize,
    /** The board's own box (the area less the strip kept free under it), which its tips come from. */
    boardSize: IntSize,
    /** How far the board's box starts from the area's left edge (px): room kept at its sides on a tablet. */
    boardLeft: Int = 0,
    rotation: Int,
    onToggleHear: (seat: Int) -> Unit,
    onLongPress: (seat: Int) -> Unit,
    own: @Composable (LudoPlayerCardUi, Modifier) -> Unit,
) {
    if (boardSize.width == 0 || boardSize.height == 0) return
    val density = LocalDensity.current
    val tips = remember(boardSize, rotation) { LudoBoardProjection.fit(Size(boardSize.width.toFloat(), boardSize.height.toFloat()), rotation).tips() }
    val left = tips[0]
    val top = tips[1]
    val right = tips[2]
    val bottom = tips[3]
    val centre = Offset((left.x + right.x) / 2 + boardLeft, (top.y + bottom.y) / 2)
    val halfW = (right.x - left.x) / 2
    val halfH = (bottom.y - top.y) / 2
    // Fewer players, bigger cards (the board stays the same size).
    val scale = TableSeatPlan.scale(cards.count { !it.isMe })
    val avatarPx = with(density) { YapNPlayTheme.dimens.monoSeatAvatar.toPx() } * scale
    // A card with an emoji or mute button is wider: the buttons sit outside its name pill.
    val cardAvatar = YapNPlayTheme.dimens.ludoCardAvatar
    fun widthOf(card: LudoPlayerCardUi) = cardWidth + seatButtonsRoom(cardAvatar, card.isMe, card.canSpeak)
    // Measured card heights, so a card is kept inside the area.
    val heights = remember { mutableStateMapOf<Int, Int>() }
    cards.forEach { card ->
        val cardPx = with(density) { widthOf(card).toPx() } * scale
        // On a tablet the bigger board would put the bottom base's card out by Roll Dice: closer in, by its start square.
        val spot = if (card.corner == BoardCorner.BOTTOM_LEFT && AdaptiveScale.tabletShare > HALF) TABLET_BOTTOM_SPOT else SPOTS.getValue(card.corner)
        val spotX = centre.x + spot.x * halfW
        // On a right edge the avatar sits on the spot and the card runs right; on a left edge the card ends there.
        val startX = if (spot.x >= 0f) spotX - avatarPx / 2 else spotX + avatarPx / 2 - cardPx
        val x = startX.coerceIn(0f, (areaSize.width - cardPx).coerceAtLeast(0f))
        val h = heights[card.seat] ?: avatarPx.roundToInt()
        val y = (centre.y + spot.y * halfH - avatarPx / 2).coerceIn(0f, (areaSize.height - h).toFloat().coerceAtLeast(0f))
        val place = Modifier
            .offset { IntOffset(x.roundToInt(), y.roundToInt()) }
            .monoScaled(scale)
            .width(widthOf(card))
            .onSizeChanged { heights[card.seat] = it.height }
        if (card.isMe) {
            own(card, place)
        } else {
            TablePlayerCard(
                card = card,
                tint = card.colour.tint(),
                faded = card.left,
                badge = card.placeLabel ?: card.statusLabel,
                badgeTint = card.badgeTint(),
                onToggleHear = { onToggleHear(card.seat) },
                onLongPress = { onLongPress(card.seat) },
                modifier = place,
            )
        }
    }
}

/**
 * Where each base's card sits, × the board's half-width and half-height
 * from its centre (taken from the design's marks), one per side: left base
 * on the lower-left edge, top base on the upper-left, right base on the
 * upper-right, bottom base past the lower-right edge (clear of it, with room for the switches).
 */
private val SPOTS = mapOf(
    BoardCorner.TOP_LEFT to Offset(-1.0f, 0.25f),
    BoardCorner.TOP_RIGHT to Offset(-0.56f, -0.91f),
    BoardCorner.BOTTOM_RIGHT to Offset(0.79f, -0.48f),
    BoardCorner.BOTTOM_LEFT to Offset(0.70f, 0.80f),
)

/** The bottom base's card on a tablet: just outside the lower-right edge by its start square, clear of Roll Dice. */
private val TABLET_BOTTOM_SPOT = Offset(0.5f, 0.65f)
private const val HALF = 0.5f
