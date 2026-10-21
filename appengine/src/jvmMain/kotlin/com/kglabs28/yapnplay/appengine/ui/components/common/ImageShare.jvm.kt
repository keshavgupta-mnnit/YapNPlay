package com.kglabs28.yapnplay.appengine.ui.components.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap

@Composable
actual fun rememberImageShare(): (image: ImageBitmap, text: String) -> Unit = { _, _ -> }
