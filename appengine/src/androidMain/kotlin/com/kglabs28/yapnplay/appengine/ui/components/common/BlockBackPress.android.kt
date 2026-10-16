package com.kglabs28.yapnplay.appengine.ui.components.common

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable

@Composable
actual fun BlockBackPress(enabled: Boolean) {
    BackHandler(enabled = enabled) {
        // Intentionally empty: Back is blocked while a blocking overlay shows.
    }
}
