package com.kglabs28.yapnplay.appengine.games.royalludo.ui.components.players

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.games.royalludo.ui.screens.match.LudoPlayerCardUi

/** The user's own card with their controls (Mic, Autoplay, Roll Dice). */
@Composable
fun LudoMyCard(
    card: LudoPlayerCardUi,
    canRoll: Boolean,
    moveHint: String?,
    autoplay: Boolean,
    micOn: Boolean,
    onRoll: () -> Unit,
    onToggleAutoplay: () -> Unit,
    onToggleMic: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LudoPlayerCard(card = card, modifier = modifier) {
        LudoMyControls(
            canRoll = canRoll,
            moveHint = moveHint,
            autoplay = autoplay,
            micOn = micOn,
            onRoll = onRoll,
            onToggleAutoplay = onToggleAutoplay,
            onToggleMic = onToggleMic,
        )
    }
}
