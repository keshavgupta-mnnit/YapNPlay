package com.kglabs28.yapnplay.appengine.ui.components.emojigift

import com.kglabs28.yapnplay.appengine.resources.Res

/**
 * The animated emojis: Google's Noto animated emoji (CC BY 4.0, credited in
 * Home's settings) as Lottie files, one per number, in
 * composeResources/files/emoji ("1.json" for emoji "1"). Each file is read
 * once and kept, since the picker, every flight and every landing draw them.
 */
object EmojiGiftAnimations {
    private val loaded = mutableMapOf<String, String>()

    /** [id]'s Lottie JSON; null for a number this build has no art for. */
    suspend fun json(id: String): String? {
        if (!EmojiGiftGlyphs.isKnown(id)) return null
        return loaded[id] ?: Res.readBytes(DIR + id + EXTENSION).decodeToString().also { loaded[id] = it }
    }

    private const val DIR = "files/emoji/"
    private const val EXTENSION = ".json"
}
