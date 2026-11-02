package com.kglabs28.yapnplay.appengine.protocol

import com.kglabs28.yapnplay.appengine.protocol.transport.MessageType
import kotlinx.serialization.Serializable

/**
 * 0.11.0 MATCH channel: send an emoji to another seat at the user's table
 * ([toSeat]). The server takes the emoji's price in coins, then every viewer
 * gets [EmojiGift] (`MatchRelay`), the sender too. Answered with
 * [EmojiGiftResult]; NOT_ENOUGH_COINS when the user can't pay.
 */
@Serializable
data class SendEmojiGift(
    val matchId: String,
    val emojiId: String,
    val toSeat: Int,
)

/** 0.11.0 reply to [SendEmojiGift]: the user's coins after paying. */
@Serializable
data class EmojiGiftResult(
    val coins: Int,
    val spent: Int,
)

/** 0.11.0 `MatchRelay` payload: [fromSeat] sent [emojiId] to [toSeat]. */
@Serializable
data class EmojiGift(
    val type: String = MessageType.EMOJI_GIFT,
    val matchId: String,
    val fromSeat: Int,
    val toSeat: Int,
    val emojiId: String,
)

/** 0.11.0 `GET /coins/emoji-gifts`: the emojis on sale, in picker order, and the user's coins. */
@Serializable
data class EmojiGiftPrices(
    val coins: Int,
    val gifts: List<EmojiGiftPrice>,
)

@Serializable
data class EmojiGiftPrice(
    val id: String,
    val coins: Int,
)
