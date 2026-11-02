package com.kglabs28.yapnplay.appengine.di

import com.kglabs28.yapnplay.appengine.features.emojigift.data.EmojiGiftRepositoryImpl
import com.kglabs28.yapnplay.appengine.features.emojigift.data.remote.EmojiGiftClient
import com.kglabs28.yapnplay.appengine.features.emojigift.data.remote.EmojiGiftClientImpl
import com.kglabs28.yapnplay.appengine.features.emojigift.domain.EmojiGiftRepository
import com.kglabs28.yapnplay.appengine.features.emojigift.usecase.GetEmojiGiftMenuUseCase
import com.kglabs28.yapnplay.appengine.features.emojigift.usecase.ObserveEmojiGiftsUseCase
import com.kglabs28.yapnplay.appengine.features.emojigift.usecase.SendEmojiGiftUseCase
import com.kglabs28.yapnplay.appengine.ui.screens.emojigift.EmojiGiftViewModel
import org.koin.dsl.module

/** Emoji gifts at every game's table. */
val emojiGiftModule = module {
    single<EmojiGiftClient> { EmojiGiftClientImpl(get(), get()) }
    single<EmojiGiftRepository> { EmojiGiftRepositoryImpl(get(), get()) }
    factory { GetEmojiGiftMenuUseCase(get()) }
    factory { SendEmojiGiftUseCase(get()) }
    factory { ObserveEmojiGiftsUseCase(get()) }
    factory { EmojiGiftViewModel(get(), get(), get(), get(), get()) }
}
