package com.kglabs28.yapnplay.appengine.games.royalludo.ui.components.players

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.games.royalludo.ui.screens.match.LudoPlayerCardUi

/**
 * The card for one corner: the player sitting there (the user's own
 * card with Mic, Autoplay and Roll Dice), or empty space when nobody is.
 */
@Composable
fun LudoCornerCard(
    card: LudoPlayerCardUi?,
    canRoll: Boolean,
    autoplay: Boolean,
    micOn: Boolean,
    onRoll: () -> Unit,
    onToggleAutoplay: () -> Unit,
    onToggleMic: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (card == null) {
        Box(modifier)
        return
    }
    LudoPlayerCard(card = card, modifier = modifier.fillMaxWidth()) {
        if (card.isMe) {
            LudoMyControls(
                canRoll = canRoll,
                autoplay = autoplay,
                micOn = micOn,
                onRoll = onRoll,
                onToggleAutoplay = onToggleAutoplay,
                onToggleMic = onToggleMic,
            )
        }
    }
}
