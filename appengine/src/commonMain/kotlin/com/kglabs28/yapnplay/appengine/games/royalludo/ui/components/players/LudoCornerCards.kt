package com.kglabs28.yapnplay.appengine.games.royalludo.ui.components.players

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.kglabs28.yapnplay.appengine.games.royalludo.ui.screens.match.BoardCorner
import com.kglabs28.yapnplay.appengine.games.royalludo.ui.screens.match.LudoPlayerCardUi
import com.kglabs28.yapnplay.appengine.ui.components.table.TablePlayerCard

/**
 * Each player's card as near their own base as it can sit without
 * covering the board. The board is a diamond: the grid's bottom-left base
 * is its bottom tip, then left, top and right ([BoardCorner] TOP_LEFT,
 * TOP_RIGHT, BOTTOM_RIGHT). The board leaves a [cardWidth] column on each
 * side: the left and right cards sit level with their tips, the top
 * base's card at the top of the right column (beside the top tip's slope)
 * and the bottom base's at the foot of the left column. Every card is the
 * same width. [own] draws the user's card (with its switches).
 */
@Composable
fun BoxScope.LudoCornerCards(
    cards: List<LudoPlayerCardUi>,
    cardWidth: Dp,
    onToggleHear: (seat: Int) -> Unit,
    onLongPress: (seat: Int) -> Unit,
    own: @Composable (LudoPlayerCardUi, Modifier) -> Unit,
) {
    cards.forEach { card ->
        val place = when (card.corner) {
            BoardCorner.TOP_LEFT -> Modifier.align(Alignment.CenterStart)
            BoardCorner.BOTTOM_RIGHT -> Modifier.align(Alignment.CenterEnd)
            BoardCorner.TOP_RIGHT -> Modifier.align(Alignment.TopEnd)
            BoardCorner.BOTTOM_LEFT -> Modifier.align(Alignment.BottomStart)
        }.width(cardWidth)
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
