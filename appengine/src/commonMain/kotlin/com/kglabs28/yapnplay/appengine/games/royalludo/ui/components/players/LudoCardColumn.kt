package com.kglabs28.yapnplay.appengine.games.royalludo.ui.components.players

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.games.royalludo.ui.screens.match.LudoPlayerCardUi

/** One side of the table: the top-corner card and the bottom-corner card. */
@Composable
fun LudoCardColumn(
    top: LudoPlayerCardUi?,
    bottom: LudoPlayerCardUi?,
    canRoll: Boolean,
    autoplay: Boolean,
    micOn: Boolean,
    onRoll: () -> Unit,
    onToggleAutoplay: () -> Unit,
    onToggleMic: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        LudoCornerCard(top, canRoll, autoplay, micOn, onRoll, onToggleAutoplay, onToggleMic)
        LudoCornerCard(bottom, canRoll, autoplay, micOn, onRoll, onToggleAutoplay, onToggleMic)
    }
}
