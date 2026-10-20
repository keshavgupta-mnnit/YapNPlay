package com.kglabs28.yapnplay.appengine.ui.components.keyboard

import androidx.compose.runtime.staticCompositionLocalOf

/** The app's in-app keyboard, provided once by [AppKeyboardHost]. */
val LocalAppKeyboard = staticCompositionLocalOf { AppKeyboardController() }
