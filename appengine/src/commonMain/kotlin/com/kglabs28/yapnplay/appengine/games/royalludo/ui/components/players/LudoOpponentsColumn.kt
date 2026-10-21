package com.kglabs28.yapnplay.appengine.games.royalludo.ui.components.players

import com.kglabs28.yapnplay.appengine.ui.components.table.TablePlayerCard
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
    onLongPress: (seat: Int) -> Unit = {},
) {
    Column(
        modifier = modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.SpaceEvenly,
    ) {
        cards.forEach { card ->
            TablePlayerCard(
                card = card,
                tint = card.colour.tint(),
                faded = card.left,
                badge = card.placeLabel ?: card.statusLabel,
                badgeTint = card.badgeTint(),
                onToggleHear = { onToggleHear(card.seat) },
                onLongPress = { onLongPress(card.seat) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
