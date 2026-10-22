package com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbPanel
import com.kglabs28.yapnplay.appengine.games.citybazaar.utils.CardDeck

/**
 * A Luck or Shagun Chest table on frosted white: its name on a band of the
 * pile's colour, the rule, then each dice total (paying rows green,
 * costing rows red).
 */
@Composable
fun CbDeckPanel(panel: CbPanel.Deck, modifier: Modifier = Modifier) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val typography = YapNPlayTheme.typography
    val shape = RoundedCornerShape(dimens.cbRowRadius)
    val tint = if (panel.deck == CardDeck.LUCK) colors.CbLuck else colors.CbChest
    Column(
        modifier = modifier
            .cbGlassBlack(shape)
            .clip(shape),
        verticalArrangement = Arrangement.spacedBy(Spacing.Xxs),
    ) {
        BasicText(
            text = panel.title.uppercase(),
            style = typography.label.copy(color = colors.Background, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center),
            modifier = Modifier
                .fillMaxWidth()
                .background(tint)
                .padding(vertical = Spacing.Xxs),
        )
        Column(
            modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = Spacing.Xs).padding(bottom = Spacing.Xs),
            verticalArrangement = Arrangement.spacedBy(Spacing.Xxs),
        ) {
            BasicText(text = panel.body, style = typography.micro.copy(color = colors.TextSecondary))
            // The results on a dark sheet, like a property card's costs, divided by hairlines.
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.RoomsSmallButton, shape)
                    .padding(Spacing.Xs),
                verticalArrangement = Arrangement.spacedBy(Spacing.Xxs),
            ) {
                panel.rows.forEachIndexed { i, row ->
                    if (i > 0) Box(Modifier.fillMaxWidth().height(dimens.hairline).background(colors.IconMuted.copy(alpha = DIVIDER_ALPHA)))
                    CbDeckRow(row = row, tint = tint)
                }
            }
        }
    }
}

private const val DIVIDER_ALPHA = 0.4f
