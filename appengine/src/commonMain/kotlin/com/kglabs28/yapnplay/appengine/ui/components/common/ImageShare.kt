package com.kglabs28.yapnplay.appengine.ui.components.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap

/** Shares a picture with a message (WhatsApp, Instagram…): the platform's share sheet. */
@Composable
expect fun rememberImageShare(): (image: ImageBitmap, text: String) -> Unit
