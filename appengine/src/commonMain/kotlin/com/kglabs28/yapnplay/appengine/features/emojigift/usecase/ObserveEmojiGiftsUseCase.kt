package com.kglabs28.yapnplay.appengine.features.emojigift.usecase

import com.kglabs28.yapnplay.appengine.features.emojigift.domain.EmojiGiftFlight
import com.kglabs28.yapnplay.appengine.features.emojigift.domain.EmojiGiftRepository
import kotlinx.coroutines.flow.Flow

/** Every emoji sent at [matchId]'s table, to fly from one seat to another. */
class ObserveEmojiGiftsUseCase(private val repository: EmojiGiftRepository) {
    operator fun invoke(matchId: String): Flow<EmojiGiftFlight> = repository.flights(matchId)
}
