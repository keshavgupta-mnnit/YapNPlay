package com.kglabs28.yapnplay.appengine.di

import com.kglabs28.yapnplay.appengine.features.voice.data.VoiceRepositoryImpl
import com.kglabs28.yapnplay.appengine.features.voice.data.remote.VoiceClient
import com.kglabs28.yapnplay.appengine.features.voice.data.remote.VoiceClientImpl
import com.kglabs28.yapnplay.appengine.features.voice.domain.VoiceRepository
import com.kglabs28.yapnplay.appengine.features.voice.usecase.ObserveLocallyMutedUseCase
import com.kglabs28.yapnplay.appengine.features.voice.usecase.SetLocallyMutedUseCase
import org.koin.dsl.module

/** Voice controls shared by every game and room (local mute today). */
val voiceModule = module {
    single<VoiceClient> { VoiceClientImpl() }
    single<VoiceRepository> { VoiceRepositoryImpl(get()) }
    factory { ObserveLocallyMutedUseCase(get()) }
    factory { SetLocallyMutedUseCase(get()) }
}
