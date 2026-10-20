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
