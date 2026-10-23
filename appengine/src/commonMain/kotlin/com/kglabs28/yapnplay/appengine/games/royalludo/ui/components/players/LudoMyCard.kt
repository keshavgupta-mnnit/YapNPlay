package com.kglabs28.yapnplay.appengine.games.royalludo.ui.components.players

import com.kglabs28.yapnplay.appengine.ui.components.table.ArenaSeatSwitches
import com.kglabs28.yapnplay.appengine.ui.components.table.TablePlayerCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.games.royalludo.ui.screens.match.LudoPlayerCardUi

/** The user's own card, with Mic and Autoplay as round icons under the pill (Roll Dice is its own button). */
@Composable
fun LudoMyCard(
    card: LudoPlayerCardUi,
    autoplay: Boolean,
    micOn: Boolean,
    onToggleAutoplay: () -> Unit,
    onToggleMic: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TablePlayerCard(
        card = card,
        tint = card.colour.tint(),
        faded = card.left,
        badge = card.placeLabel ?: card.statusLabel,
        badgeTint = card.badgeTint(),
        modifier = modifier,
    ) {
        ArenaSeatSwitches(
            micOn = micOn,
            autoplay = autoplay,
            onToggleMic = onToggleMic,
            onToggleAutoplay = onToggleAutoplay,
        )
    }
}
