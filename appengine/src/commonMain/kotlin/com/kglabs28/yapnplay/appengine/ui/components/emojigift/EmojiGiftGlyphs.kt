package com.kglabs28.yapnplay.appengine.ui.components.emojigift

/**
 * Which emoji each of the server's numbered emojis ("1" to "9", in picker
 * order) is: the server owns the numbers and prices, the app the art, so
 * the art can change in an app update. Each is drawn animated
 * ([EmojiGiftAnimations]); this still glyph shows until its animation is
 * read, and for a number this build doesn't know (a smile).
 */
object EmojiGiftGlyphs {
    private val glyphs = mapOf(
        "1" to "🤣",
        "2" to "😭",
        "3" to "😍",
        "4" to "😱",
        "5" to "🤬",
        "6" to "🤯",
        "7" to "🤑",
        "8" to "😎",
        "9" to "👿",
    )

    fun of(id: String): String = glyphs[id] ?: FALLBACK

    /** This build has art for [id]. */
    fun isKnown(id: String): Boolean = id in glyphs

    private const val FALLBACK = "🙂"
}
