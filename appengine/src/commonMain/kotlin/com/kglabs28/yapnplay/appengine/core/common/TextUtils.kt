package com.kglabs28.yapnplay.appengine.core.common

/**
 * Pure text helpers. Input sanitizing and formatting live here —
 * never inside a @Composable file, which owns UI only.
 */
object TextUtils {

    /**
     * Caps [text] at [maxLength] by truncating overflow, so fast typing
     * and pasting behave the same. Negative [maxLength] disables capping.
     */
    fun capLength(text: String, maxLength: Int): String =
        if (maxLength < 0) text else text.take(maxLength)
}
