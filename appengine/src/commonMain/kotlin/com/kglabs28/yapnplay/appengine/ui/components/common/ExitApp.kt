package com.kglabs28.yapnplay.appengine.ui.components.common

import androidx.compose.runtime.Composable

/** Closes the app (Android: finishes the activity). Other platforms have no such exit. */
@Composable
expect fun rememberExitApp(): () -> Unit
