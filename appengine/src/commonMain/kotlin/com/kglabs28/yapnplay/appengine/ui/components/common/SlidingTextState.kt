package com.kglabs28.yapnplay.appengine.ui.components.common

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Which tap-to-slide text is sliding (at most one). Any touch stops it
 * (SlidingTextHost); a tap on a text starts it, a second tap stops it.
 */
class SlidingTextState {
    var active: Any? by mutableStateOf(null)
        private set
    private var stoppedByTouch: Any? = null

    /** Every touch down, anywhere (before the text sees its tap). */
    fun onAnyTouch() {
        stoppedByTouch = active
        active = null
    }

    fun tapped(key: Any) {
        active = if (stoppedByTouch === key) null else key
        stoppedByTouch = null
    }
}

val LocalSlidingText = staticCompositionLocalOf { SlidingTextState() }
