package com.kglabs28.yapnplay.appengine.ui.components.keyboard

/**
 * A text field asking for the in-app keyboard: [owner] is the field, [value]
 * its text now, [hint] shown above the keys while it's empty. [slots]: show
 * the text as letter slots of that pattern (a word to guess, see LetterSlots);
 * [error]: the last entry was wrong (red until the next key); [compact]: a
 * shorter keyboard, so the screen keeps more of itself in view.
 */
data class AppKeyboardRequest(
    val owner: Any,
    val hint: String,
    val value: String,
    val maxLength: Int,
    val onChange: (String) -> Unit,
    val onDone: () -> Unit,
    val slots: String? = null,
    val error: Boolean = false,
    val compact: Boolean = false,
)
