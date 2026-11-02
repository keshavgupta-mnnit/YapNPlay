package com.kglabs28.yapnplay.appengine.features.emojigift.usecase

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.features.emojigift.domain.EmojiGiftMenu
import com.kglabs28.yapnplay.appengine.features.emojigift.domain.EmojiGiftRepository

/** The emojis on sale with their prices, and the user's coins. */
class GetEmojiGiftMenuUseCase(private val repository: EmojiGiftRepository) {
    suspend operator fun invoke(): AppResult<EmojiGiftMenu> = repository.menu()
}
