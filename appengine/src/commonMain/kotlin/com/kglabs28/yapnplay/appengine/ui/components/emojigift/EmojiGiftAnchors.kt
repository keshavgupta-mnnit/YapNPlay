package com.kglabs28.yapnplay.appengine.ui.components.emojigift

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Rect

/**
 * Where every seat's avatar is on screen (root coordinates), so an emoji can
 * fly from one to another, and which seat is the user's (no seat: watching,
 * so nothing to send from).
 */
@Stable
class EmojiGiftAnchors {
    private val avatars = mutableStateMapOf<Int, Rect>()

    var mySeat: Int? by mutableStateOf(null)
        private set

    fun place(seat: Int, bounds: Rect, isMe: Boolean) {
        avatars[seat] = bounds
        if (isMe) mySeat = seat
    }

    /** The seat is no longer drawn. */
    fun forget(seat: Int) {
        avatars.remove(seat)
        if (mySeat == seat) mySeat = null
    }

    fun avatar(seat: Int): Rect? = avatars[seat]
}
