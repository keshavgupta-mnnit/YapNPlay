package com.kglabs28.yapnplay.appengine.games.doodleking.ui.components

/**
 * The guess box's letters against the hint (`_` per hidden letter, given
 * letters as they are): the user types only the hidden letters, in order,
 * and the hint's letters stay where they are.
 */
object DoodleGuessSlots {

    /** How many letters the user types: the hidden ones. */
    fun blanks(pattern: String): Int = pattern.count { it == BLANK }

    /**
     * The word as the box shows it: the hint's letters in their places, the typed letters in the
     * hidden ones in order, `_` for a hidden one not typed yet; spaces and hyphens as they are.
     * A whole word typed in the phone's own keyboard shows as it is.
     */
    fun display(pattern: String, typed: String): String {
        if (typed.length > blanks(pattern)) return typed.uppercase()
        var next = 0
        return pattern.map { c -> if (c == BLANK) typed.getOrNull(next++)?.uppercaseChar() ?: BLANK else c.uppercaseChar() }.joinToString("")
    }

    /**
     * The guess to send: the hint's letters with the typed ones in the hidden places. A guess
     * longer than the hidden letters is a whole word (typed in the phone's own keyboard) and goes as it is.
     */
    fun guess(pattern: String, typed: String): String {
        if (typed.length > blanks(pattern)) return typed
        var next = 0
        return buildString {
            pattern.forEach { c ->
                when {
                    c == BLANK -> typed.getOrNull(next++)?.let(::append)
                    c.isLetterOrDigit() -> append(c)
                }
            }
        }
    }

    /**
     * The typed letters after a new hint ([before] → [after]): each stays in its place, and one where
     * a letter was just given is dropped (the hint shows it now).
     */
    fun refit(before: String, after: String, typed: String): String {
        if (before.length != after.length) return ""
        val placed = arrayOfNulls<Char>(before.length)
        var next = 0
        before.forEachIndexed { i, c -> if (c == BLANK && next < typed.length) placed[i] = typed[next++] }
        return buildString {
            after.forEachIndexed { i, c -> if (c == BLANK) placed[i]?.let(::append) ?: return@buildString }
        }
    }

    private const val BLANK = '_'
}
