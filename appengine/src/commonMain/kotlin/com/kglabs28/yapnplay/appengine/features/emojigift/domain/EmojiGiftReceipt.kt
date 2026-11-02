package com.kglabs28.yapnplay.appengine.features.emojigift.domain

/** A paid emoji: the user's [coins] left and what it cost ([spent]). */
data class EmojiGiftReceipt(val coins: Int, val spent: Int)
