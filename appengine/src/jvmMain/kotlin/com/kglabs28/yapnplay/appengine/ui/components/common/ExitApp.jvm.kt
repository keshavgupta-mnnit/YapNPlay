package com.kglabs28.yapnplay.appengine.ui.components.common

import androidx.compose.runtime.Composable

/** JVM has no system Back, so nothing asks to exit. */
@Composable
actual fun rememberExitApp(): () -> Unit = {}
