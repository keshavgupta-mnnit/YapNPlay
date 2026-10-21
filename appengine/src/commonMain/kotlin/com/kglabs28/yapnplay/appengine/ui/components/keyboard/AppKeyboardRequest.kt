package com.kglabs28.yapnplay.appengine.ui.components.keyboard

/**
 * A text field asking for the in-app keyboard: [owner] is the field, [value]
 * its text now, [hint] shown above the keys while it's empty.
 */
data class AppKeyboardRequest(
    val owner: Any,
    val hint: String,
    val value: String,
    val maxLength: Int,
    val onChange: (String) -> Unit,
    val onDone: () -> Unit,
)
