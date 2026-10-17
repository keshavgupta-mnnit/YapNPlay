package com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.dialogs

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.ui.components.table.tableFrostedGlass
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.board.tileHeaderTint
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.board.tint
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbPropertyCardUi

/**
 * A city's title card, one frosted-white card as wide as its content: its colour header with the name, rent per tier
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
            .width(IntrinsicSize.Max)
            .background(colors.Background, shape)
            .tableFrostedGlass(tint = colors.AvatarRingSilver, shape = shape, radius = dimens.cbRowRadius, glow = dimens.zero)
            .clip(shape),
        verticalArrangement = Arrangement.spacedBy(Spacing.Xxs),
    ) {
        Box(Modifier.fillMaxWidth().background(header).padding(vertical = Spacing.Xxs)) {
            BasicText(
                text = card.name.uppercase(),
                style = typography.label.copy(color = colors.Background, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center),
                modifier = Modifier.fillMaxWidth(),
            )
        }
        // Filling a given height: the rows scroll and the owner bar stays at the bottom.
        val body = if (fillHeight) Modifier.weight(1f).verticalScroll(rememberScrollState()) else Modifier
        Column(modifier = body.padding(horizontal = Spacing.Xs).padding(bottom = Spacing.Xs), verticalArrangement = Arrangement.spacedBy(Spacing.Xxs)) {
            CbRateTable(rows = card.rows)
            card.income?.let { BasicText(text = it, style = typography.label.copy(color = colors.CbCash, fontWeight = FontWeight.Bold)) }
            card.sale?.let {
                BasicText(
                    text = it,
                    style = typography.label.copy(color = colors.CbGain),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(dimens.hairline, colors.CbGain, shape)
                        .padding(horizontal = Spacing.Xs, vertical = Spacing.Xxxs),
                )
            }
            if (card.costs.isNotEmpty()) CbSheetTable(rows = card.costs)
            if (card.owner.isNotEmpty() && card.ownerColour == null) {
                BasicText(text = card.owner, style = typography.micro.copy(color = colors.TextSecondary))
            }
        }
        card.ownerColour?.let { colour ->
            BasicText(
                text = card.owner,
                style = typography.label.copy(color = colors.TextPrimary, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center),
                modifier = Modifier.fillMaxWidth().background(colour.tint()).padding(vertical = Spacing.Xxs),
            )
        }
    }
}
