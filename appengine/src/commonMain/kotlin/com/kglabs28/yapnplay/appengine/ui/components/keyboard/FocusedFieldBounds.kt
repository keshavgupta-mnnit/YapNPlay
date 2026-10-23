package com.kglabs28.yapnplay.appengine.ui.components.keyboard

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.geometry.Rect

/** Where the text box typing with the phone's keyboard is on screen (window px), or null. */
class FocusedFieldBounds {
    private var owner: Any? = null

    var bounds: Rect? = null
        private set

    fun set(token: Any, rect: Rect) {
        owner = token
        bounds = rect
    }

    fun clear(token: Any) {
        if (owner !== token) return
        owner = null
        bounds = null
    }
}

val LocalFocusedField = staticCompositionLocalOf { FocusedFieldBounds() }
