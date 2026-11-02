package com.kglabs28.yapnplay.appengine.features.emojigift.data.remote

import com.kglabs28.yapnplay.appengine.protocol.EmojiGift
import com.kglabs28.yapnplay.appengine.protocol.EmojiGiftPrices
import com.kglabs28.yapnplay.appengine.protocol.EmojiGiftResult
import kotlinx.coroutines.flow.Flow

/** Emoji gifts on the wire: the prices over HTTP; sending, and the table's gifts, on the match channel. */
interface EmojiGiftClient {
    /** `GET /coins/emoji-gifts` */
    suspend fun prices(): EmojiGiftPrices

    /** `SendEmojiGift`, answered with `EmojiGiftResult`. */
    suspend fun send(matchId: String, emojiId: String, toSeat: Int): EmojiGiftResult

    /** Every `MatchRelay` of type `EmojiGift` for [matchId]. */
    fun gifts(matchId: String): Flow<EmojiGift>
}
