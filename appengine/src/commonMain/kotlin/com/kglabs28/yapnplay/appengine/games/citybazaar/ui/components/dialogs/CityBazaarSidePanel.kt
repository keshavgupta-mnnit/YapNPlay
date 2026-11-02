package com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.dialogs

import com.kglabs28.yapnplay.appengine.ui.components.ads.AdPill
import androidx.compose.foundation.layout.fillMaxWidth
import com.kglabs28.yapnplay.appengine.ui.components.common.verticalScrollWithBar
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.board.tint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.players.CbCityCard
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbPanel

/**
 * The info panel in the left column, in place of the other players: a
 * tile's card, a player, a Luck / Shagun Chest table, or the user's cities.
 * It fills the column; taps on it stay on it. A city's card, a player, the
 * bank and the user's cities carry an ad at the bottom, as wide as the card.
 */
@Composable
fun CityBazaarSidePanel(
    panel: CbPanel,
    onPropertyClick: (tile: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.clickable(interactionSource = null, indication = null) {}) {
        val fill = Modifier.fillMaxSize()
        val ad: @Composable () -> Unit = { AdPill(modifier = Modifier.fillMaxWidth(), reserveSpace = true) }
        when (panel) {
            is CbPanel.Property -> CbPropertyCard(card = panel.card, modifier = fill, fillHeight = true, aboveOwner = ad)
            is CbPanel.Player -> CbPlayerPanel(panel.name, panel.colour.tint(), panel.sheet, panel.cities, onPropertyClick = onPropertyClick, modifier = fill, footer = ad)
            is CbPanel.Bank -> CbPlayerPanel(panel.name, YapNPlayTheme.colors.Gold, panel.sheet, panel.cities, onPropertyClick = onPropertyClick, modifier = fill, footer = ad)
            is CbPanel.Deck -> CbDeckPanel(panel = panel, modifier = fill)
            // Compact cards in the group colours, like the player pods; a tap opens that one's full card.
            // On the same glassy black as the other panels.
            is CbPanel.Cities -> Column(
                modifier = fill
                    .cbGlassBlack(RoundedCornerShape(YapNPlayTheme.dimens.cbRowRadius))
                    .padding(Spacing.Xs),
                verticalArrangement = Arrangement.spacedBy(Spacing.Xs),
            ) {
                Column(modifier = Modifier.weight(1f).verticalScrollWithBar(), verticalArrangement = Arrangement.spacedBy(Spacing.Xs)) {
                    panel.cities.forEach { CbCityCard(city = it, onClick = { onPropertyClick(it.tile) }) }
                }
                ad()
            }
        }
    }
}
