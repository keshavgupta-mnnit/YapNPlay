package com.kglabs28.yapnplay.appengine.ui.components.table

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.Strings

/**
 * The shared MONO table furniture. Both MONO games render the same
 * table, so the piles, opponent pods and hand row live here once and
 * each game only supplies how a card of its own type is drawn.
 */

/** One opponent's public row: their card count and whether it is their turn. */
data class MonoOpponentUi(
    val seat: Int,
    val name: String,
    val cardCount: Int,
    val isActive: Boolean,
)

/**
 * The table centre: the face-down draw pile with its count, the discard
 * pile showing the face card in the colour in force, and any penalty
 * waiting to be stacked onto. [faceCard] draws the discard pile's card.
 */
@Composable
fun MonoTableCentre(
    drawPileCount: Int,
    discardPileCount: Int,
    activeStackPenalty: Int,
    faceCard: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Spacing.Md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MonoPile(
            count = drawPileCount,
            label = Strings.monoDrawPile,
            modifier = Modifier.width(YapNPlayTheme.dimens.monoPileCardWidth),
        ) { MonoCardFace(glyph = "", cardColor = prismFace, faceDown = true, modifier = Modifier.fillMaxWidth()) }
        MonoPile(
            count = discardPileCount,
            label = Strings.monoDiscardPile,
            modifier = Modifier.width(YapNPlayTheme.dimens.monoPileCardWidth),
            content = faceCard,
        )
        if (activeStackPenalty > 0) {
            BasicText(
                text = "+$activeStackPenalty",
                style = YapNPlayTheme.typography.headline.copy(
                    color = YapNPlayTheme.colors.Error,
                    fontWeight = FontWeight.Bold,
                ),
            )
        }
    }
}

/** One centre pile: [content] draws its card, under it the count and name. */
@Composable
private fun MonoPile(
    count: Int,
    label: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val colors = YapNPlayTheme.colors
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.Xxs),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(YapNPlayTheme.shapes.panel)
                .background(colors.Panel)
                .padding(Spacing.Xxs),
            contentAlignment = Alignment.Center,
        ) {
            content()
        }
        BasicText(
            text = count.toString(),
            style = YapNPlayTheme.typography.label.copy(
                color = colors.TextPrimary,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            ),
        )
        BasicText(
            text = label,
            style = YapNPlayTheme.typography.micro.copy(
                color = colors.TextSecondary,
                textAlign = TextAlign.Center,
            ),
        )
    }
}

/**
 * The opponents' pods, in the order the table shows them so they do not
 * jump around between updates. The active seat's pod is lit.
 */
@Composable
fun MonoOpponentRow(
    opponents: List<MonoOpponentUi>,
    modifier: Modifier = Modifier,
) {
    if (opponents.isEmpty()) return
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(Spacing.Xs),
    ) {
        opponents.forEach { MonoOpponentPod(it) }
    }
}

/** One opponent: a face-down card, their name, and how many they hold. */
@Composable
private fun MonoOpponentPod(opponent: MonoOpponentUi) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    Column(
        modifier = Modifier.width(dimens.monoOpponentWidth),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.Xxs),
    ) {
        MonoCardFace(
            glyph = "",
            cardColor = prismFace,
            faceDown = true,
            isSelected = opponent.isActive,
            modifier = Modifier.width(dimens.monoPileCardWidth),
        )
        BasicText(
            text = opponent.name,
            style = YapNPlayTheme.typography.micro.copy(
                color = if (opponent.isActive) colors.GameZoneAccent else colors.TextSecondary,
                fontWeight = if (opponent.isActive) FontWeight.Bold else FontWeight.Normal,
                textAlign = TextAlign.Center,
            ),
            maxLines = 1,
        )
        BasicText(
            text = opponent.cardCount.toString(),
            style = YapNPlayTheme.typography.micro.copy(
                color = colors.TextPrimary,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            ),
        )
    }
}

/**
 * The local player's hand as a scrolling row. [card] draws one card at
 * its hand index, so the screen decides which one is selected.
 */
@Composable
fun MonoHandRow(
    handSize: Int,
    modifier: Modifier = Modifier,
    card: @Composable (Int) -> Unit,
) {
    if (handSize == 0) return
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(Spacing.Xs),
    ) {
        repeat(handSize) { index -> card(index) }
    }
}