package com.kglabs28.yapnplay.appengine.features.emojigift.domain

/** The emojis on sale, in picker order, and the user's [coins] now. */
data class EmojiGiftMenu(val coins: Int, val offers: List<EmojiGiftOffer>)
