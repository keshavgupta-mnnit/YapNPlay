package com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.dialogs

import com.kglabs28.yapnplay.appengine.ui.components.common.verticalScrollWithBar
import com.kglabs28.yapnplay.appengine.ui.components.common.ShrinkText
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.board.tileHeaderTint
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.board.tint
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbPropertyCardUi

/**
 * A city's title card, one glassy black card as wide as its content: its colour header with the name, rent per tier
 * (current one highlighted), what it earns now (2X with 3 of a colour),
 * what the next sale brings (highlighted), its costs, and its owner in a
 * bold bar of their colour.
 */
@Composable
fun CbPropertyCard(card: CbPropertyCardUi, modifier: Modifier = Modifier, fillHeight: Boolean = false) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val typography = YapNPlayTheme.typography
    val shape = RoundedCornerShape(dimens.cbRowRadius)
    val header = tileHeaderTint(card.group, card.kind)
    Column(
        modifier = modifier
            .widthIn(min = dimens.cbPropertyCardMinWidth)
            // As wide as its content, except in the side column, which it fills edge to edge.
            .then(if (fillHeight) Modifier else Modifier.width(IntrinsicSize.Max))
            .cbGlassBlack(shape)
            .clip(shape),
        verticalArrangement = Arrangement.spacedBy(Spacing.Xxs),
    ) {
        Box(Modifier.fillMaxWidth().background(header).padding(vertical = Spacing.Xxs)) {
            ShrinkText(
                text = card.name.uppercase(),
                style = typography.label.copy(color = colors.Background, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center),
                modifier = Modifier.fillMaxWidth(),
            )
        }
        // Filling a given height: the rows scroll and the owner bar stays at the bottom.
        val body = if (fillHeight) Modifier.weight(1f).verticalScrollWithBar() else Modifier
        Column(modifier = body.padding(horizontal = Spacing.Xs).padding(bottom = Spacing.Xs), verticalArrangement = Arrangement.spacedBy(Spacing.Xxs)) {
            CbRateTable(rows = card.rows)
            card.income?.let { ShrinkText(text = it, style = typography.label.copy(color = colors.CbCash, fontWeight = FontWeight.Bold)) }
            card.collected?.let { ShrinkText(text = it, style = typography.label.copy(color = colors.TextSecondary)) }
            card.sale?.let {
                ShrinkText(
                    text = it,
                    style = typography.label.copy(color = colors.CbGain),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(dimens.hairline, colors.CbGain, shape)
                        .padding(horizontal = Spacing.Xs, vertical = Spacing.Xxxs),
                )
            }
            if (card.costs.isNotEmpty()) CbSheetTable(rows = card.costs)
        }
        // Bottom bar: the owner on their colour, or "Available for sale" on dark grey (none for tiles that can't be owned).
        if (card.owner.isNotEmpty()) {
            ShrinkText(
                text = card.owner,
                style = typography.label.copy(color = colors.TextPrimary, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center),
                modifier = Modifier.fillMaxWidth().background(card.ownerColour?.tint() ?: colors.CbUnownedBar).padding(vertical = Spacing.Xxs),
            )
        }
    }
}
