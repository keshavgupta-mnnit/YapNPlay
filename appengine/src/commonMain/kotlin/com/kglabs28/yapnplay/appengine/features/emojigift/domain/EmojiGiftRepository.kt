package com.kglabs28.yapnplay.appengine.features.emojigift.domain

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import kotlinx.coroutines.flow.Flow

/**
 * Emojis players send each other at a table for coins. The server sets the
 * prices and takes the coins; NOT_ENOUGH_COINS comes back as
 * YapNPlayError.LimitReached (the emoji's price).
 */
interface EmojiGiftRepository {
    suspend fun menu(): AppResult<EmojiGiftMenu>
    suspend fun send(matchId: String, emojiId: String, toSeat: Int, price: Int): AppResult<EmojiGiftReceipt>
    /** Every emoji sent at [matchId]'s table, as the server passes them on. */
    fun flights(matchId: String): Flow<EmojiGiftFlight>
}
