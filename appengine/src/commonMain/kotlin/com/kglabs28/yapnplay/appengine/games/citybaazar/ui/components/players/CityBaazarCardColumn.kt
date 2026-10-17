package com.kglabs28.yapnplay.appengine.games.citybaazar.ui.components.players

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.games.citybaazar.ui.screens.match.CbPlayerCardUi

/** A side column of player pods, top down, with [footer] pinned to the bottom (the user's pod, or the action buttons). */
@Composable
fun CityBaazarCardColumn(
    cards: List<CbPlayerCardUi>,
    onToggleHear: (seat: Int) -> Unit,
    onCardClick: (seat: Int) -> Unit,
    modifier: Modifier = Modifier,
    footer: @Composable ColumnScope.() -> Unit = {},
) {
    Column(modifier = modifier.fillMaxHeight(), verticalArrangement = Arrangement.spacedBy(Spacing.Xs)) {
        cards.forEach { card ->
            CityBaazarPlayerCard(card = card, onToggleHear = { onToggleHear(card.seat) }, onClick = { onCardClick(card.seat) }, modifier = Modifier.fillMaxWidth())
        }
        Spacer(Modifier.weight(1f))
        footer()
    }
}
