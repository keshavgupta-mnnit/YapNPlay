package com.kglabs28.yapnplay.appengine.ui.components.keyboard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * The one in-app keyboard: which field it types into (or none). The app
 * never opens the phone's own keyboard; fields call [open] on a tap.
 */
class AppKeyboardController {
    var request: AppKeyboardRequest? by mutableStateOf(null)
        private set

    /** Where the open field's bottom edge is on screen (px), as if the screen were not lifted. */
    var fieldBottom: Float by mutableStateOf(0f)
        private set

    /** How far the screen is lifted right now (px), so the field clears the keys. */
    var lift: Float by mutableStateOf(0f)

    /** The field [owner] is at [bottom] on screen right now (px, including any current lift). */
    fun place(owner: Any, bottom: Float) {
        if (request?.owner === owner) fieldBottom = bottom + lift
    }

    fun open(next: AppKeyboardRequest) {
        request = next
    }

    fun isOpenFor(owner: Any): Boolean = request?.owner === owner

    /** The field's text changed (typing, its × or the screen): keep the keyboard's copy in step. */
    fun sync(owner: Any, value: String) {
        val now = request ?: return
        if (now.owner === owner && now.value != value) request = now.copy(value = value)
    }

    fun type(value: String) {
        val now = request ?: return
        val next = value.take(now.maxLength)
        request = now.copy(value = next)
        now.onChange(next)
    }

    fun done() {
        val now = request ?: return
        request = null
        now.onDone()
    }

    fun close(owner: Any? = null) {
        if (owner == null || request?.owner === owner) request = null
    }
}
