package com.kglabs28.yapnplay.appengine.ui.screens.emojigift

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.ui.components.emojigift.EmojiGiftAnchors
import com.kglabs28.yapnplay.appengine.ui.components.emojigift.EmojiGiftFlightLayer
import com.kglabs28.yapnplay.appengine.ui.components.emojigift.EmojiGiftTable
import com.kglabs28.yapnplay.appengine.ui.components.emojigift.LocalEmojiGiftTable

/**
 * Emoji gifts over a game's table ([content]), the same for every game: the
 * seats inside get the emoji button, the picker and the avatar swap through
 * [LocalEmojiGiftTable], and the emojis fly over everything in [content].
 * Dialogs drawn after this layer stay on top of the flights.
 */
@Composable
fun EmojiGiftLayer(
    matchId: String,
    viewModel: EmojiGiftViewModel?,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    if (viewModel == null) {
        content()
        return
    }
    LaunchedEffect(viewModel, matchId) { viewModel.onIntent(EmojiGiftIntent.Start(matchId)) }
    val state = viewModel.state.collectAsState()
    val anchors = remember { EmojiGiftAnchors() }
    val table = remember(viewModel, anchors) {
        EmojiGiftTable(
            anchors = anchors,
            ui = derivedStateOf { state.value.table },
            onOpen = { viewModel.onIntent(EmojiGiftIntent.Open(it)) },
            onClose = { viewModel.onIntent(EmojiGiftIntent.Close) },
            onPick = { viewModel.onIntent(EmojiGiftIntent.Pick(it)) },
        )
    }
    CompositionLocalProvider(LocalEmojiGiftTable provides table) {
        Box(modifier = modifier.fillMaxSize()) {
            content()
            EmojiGiftFlightLayer(flights = table.ui.value.flights, anchors = anchors)
        }
    }
}
