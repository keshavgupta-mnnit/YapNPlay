package com.kglabs28.yapnplay.appengine.ui.components.keyboard

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInWindow

/** The text box typing with the phone's keyboard (its place on screen is read when asked), or none. */
class FocusedFieldBounds {
    private var owner: Any? = null
    private var coordinates: LayoutCoordinates? = null

    /** Where that box is right now (window px); null when none is focused or it left the screen. */
    val bounds: Rect? get() = coordinates?.takeIf { it.isAttached }?.boundsInWindow()

    fun set(token: Any, at: LayoutCoordinates?) {
        owner = token
        coordinates = at
    }

    fun clear(token: Any) {
        if (owner !== token) return
        owner = null
        coordinates = null
    }
}

val LocalFocusedField = staticCompositionLocalOf { FocusedFieldBounds() }
