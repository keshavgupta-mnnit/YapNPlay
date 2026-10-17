package com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbPanel

/** The info panel over the other players: a tile's card, or a player and their properties. Taps on it stay on it. */
@Composable
fun CityBazaarSidePanel(
    panel: CbPanel,
    onPropertyClick: (tile: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .widthIn(max = YapNPlayTheme.dimens.cbPanelWidth)
            .clickable(interactionSource = null, indication = null) {}
            .verticalScroll(rememberScrollState()),
    ) {
        when (panel) {
            is CbPanel.Property -> CbPropertyCard(card = panel.card)
            is CbPanel.Player -> CbPlayerPanel(panel = panel, onPropertyClick = onPropertyClick)
        }
    }
}
