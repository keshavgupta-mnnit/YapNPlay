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

    /**
     * Groups thousands with commas: 4250 -> "4,250". Balances and scores
     * read from here so every screen formats numbers the same way.
     */
    /**
     * Normalizes room-code typing: upper-case, letters and digits only,
     * capped at [maxLength]. Paste and fast typing behave the same.
     */
    fun roomCode(input: String, maxLength: Int): String =
        capLength(input.uppercase().filter { it.isLetterOrDigit() }, maxLength)

    fun groupedThousands(value: Int): String {
        val digits = value.toString()
        if (digits.length <= 3) return digits
        val remainder = digits.length % 3
        val head = if (remainder == 0) "" else digits.take(remainder) + ","
        return head + digits.drop(remainder).chunked(3).joinToString(",")
    }
}
