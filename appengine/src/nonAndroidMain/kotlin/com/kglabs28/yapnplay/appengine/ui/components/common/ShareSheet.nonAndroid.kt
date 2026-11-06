package com.kglabs28.yapnplay.appengine.ui.components.common

import androidx.compose.runtime.Composable

/** JVM has no share sheet. */
@Composable
actual fun rememberShareSheet(): (text: String) -> Unit = { }
