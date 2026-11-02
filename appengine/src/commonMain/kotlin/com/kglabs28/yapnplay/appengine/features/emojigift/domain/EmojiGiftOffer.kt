package com.kglabs28.yapnplay.appengine.features.emojigift.domain

/** One emoji on sale at the table: its [id] (the art the app draws) and its price in [coins]. */
data class EmojiGiftOffer(val id: String, val coins: Int)
