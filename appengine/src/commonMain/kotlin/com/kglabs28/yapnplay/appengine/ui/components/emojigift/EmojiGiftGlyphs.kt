package com.kglabs28.yapnplay.appengine.ui.components.emojigift

/**
 * Which emoji each of the server's numbered emojis ("1" to "8", in picker
 * order) shows: the server owns the numbers and prices, the app the art, so
 * the art can change in an app update. Today the phone's own emoji font
 * draws them; the animated art replaces this in one place ([EmojiGiftArt]).
 * A number this build doesn't know shows a smile.
 */
object EmojiGiftGlyphs {
    private val glyphs = mapOf(
        "1" to "😂",
        "2" to "🤣",
        "3" to "🤡",
        "4" to "🤯",
        "5" to "😜",
        "6" to "😡",
        "7" to "😍",
        "8" to "🤦",
    )

    fun of(id: String): String = glyphs[id] ?: FALLBACK

    private const val FALLBACK = "🙂"
}
