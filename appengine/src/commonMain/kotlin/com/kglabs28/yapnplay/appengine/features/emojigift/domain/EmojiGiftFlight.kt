package com.kglabs28.yapnplay.appengine.features.emojigift.domain

/** An emoji the server says [fromSeat] sent [toSeat] at this table (the user's own ones too). */
data class EmojiGiftFlight(val fromSeat: Int, val toSeat: Int, val emojiId: String)
