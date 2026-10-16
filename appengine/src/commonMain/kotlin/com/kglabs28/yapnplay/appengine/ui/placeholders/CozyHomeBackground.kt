package com.kglabs28.yapnplay.appengine.ui.placeholders

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Home screen background: neon room art with the content on top.
 * Swap the drawable when final art lands — nothing else changes.
 */
@Composable
fun CozyHomeBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(modifier = modifier.fillMaxSize()) {
        HomeBackgroundArtwork(modifier = Modifier.fillMaxSize())
        content()
    }
}
