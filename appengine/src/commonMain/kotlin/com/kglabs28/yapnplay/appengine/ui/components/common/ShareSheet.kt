package com.kglabs28.yapnplay.appengine.ui.components.common

import androidx.compose.runtime.Composable

/**
 * Returns a function that opens the system share sheet (WhatsApp,
 * Telegram, SMS…) with a piece of text, e.g. a room invite link.
 */
@Composable
expect fun rememberShareSheet(): (text: String) -> Unit
