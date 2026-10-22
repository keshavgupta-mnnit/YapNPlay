package com.kglabs28.yapnplay.appengine.ui.placeholders

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/** The arena backdrop for the board games (Royal Ludo, City Royale): the lounge with its rug, then [content] over it. */
@Composable
fun GameArenaBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(modifier = modifier.fillMaxSize()) {
        GameArenaArtwork(modifier = Modifier.fillMaxSize())
        content()
    }
}
