package com.kglabs28.yapnplay.appengine.ui.components.common

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable

@Composable
actual fun BlockBackPress(enabled: Boolean, onBack: () -> Unit) {
    // Back never reaches the screen behind a blocking overlay.
    BackHandler(enabled = enabled, onBack = onBack)
}
