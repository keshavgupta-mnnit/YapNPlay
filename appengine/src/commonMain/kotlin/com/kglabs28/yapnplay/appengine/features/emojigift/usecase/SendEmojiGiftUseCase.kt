package com.kglabs28.yapnplay.appengine.features.emojigift.usecase

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.features.emojigift.domain.EmojiGiftOffer
import com.kglabs28.yapnplay.appengine.features.emojigift.domain.EmojiGiftReceipt
import com.kglabs28.yapnplay.appengine.features.emojigift.domain.EmojiGiftRepository

/** Buys [offer] for [toSeat] at [matchId]'s table; the flight reaches everyone (the user too) through ObserveEmojiGiftsUseCase. */
class SendEmojiGiftUseCase(private val repository: EmojiGiftRepository) {
    suspend operator fun invoke(matchId: String, offer: EmojiGiftOffer, toSeat: Int): AppResult<EmojiGiftReceipt> =
        repository.send(matchId, offer.id, toSeat, offer.coins)
}
