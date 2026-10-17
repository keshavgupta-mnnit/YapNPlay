package com.kglabs28.yapnplay.appengine.games.royalludo.ui.components.players

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.games.royalludo.ui.screens.match.LudoPlayerCardUi

/** The other players' cards stacked on the left, in turn order (clockwise from the user). */
@Composable
fun LudoOpponentsColumn(
    cards: List<LudoPlayerCardUi>,
    onToggleHear: (seat: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.SpaceEvenly,
    ) {
        cards.forEach { card ->
            LudoPlayerCard(
                card = card,
                onToggleHear = { onToggleHear(card.seat) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
