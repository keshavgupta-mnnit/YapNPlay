package com.kglabs28.yapnplay.appengine.di

import com.kglabs28.yapnplay.appengine.features.voice.data.VoiceRepositoryImpl
import com.kglabs28.yapnplay.appengine.features.voice.data.remote.VoiceClient
import com.kglabs28.yapnplay.appengine.features.voice.data.remote.VoiceClientImpl
import com.kglabs28.yapnplay.appengine.features.voice.domain.VoiceRepository
import com.kglabs28.yapnplay.appengine.features.voice.usecase.ObserveLocallyMutedUseCase
import com.kglabs28.yapnplay.appengine.features.voice.usecase.ObserveMicOnUseCase
import com.kglabs28.yapnplay.appengine.features.voice.usecase.ObserveMySpeakingUseCase
import com.kglabs28.yapnplay.appengine.features.voice.usecase.SetLocallyMutedUseCase
import com.kglabs28.yapnplay.appengine.features.voice.usecase.SetMicOnUseCase
import com.kglabs28.yapnplay.appengine.features.voice.usecase.SetMicOpenUseCase
import org.koin.dsl.module

/** Voice controls shared by every game and room: local mute, and the user's own mic. */
val voiceModule = module {
    single<VoiceClient> { VoiceClientImpl(get()) }
    single<VoiceRepository> { VoiceRepositoryImpl(get()) }
    factory { ObserveLocallyMutedUseCase(get()) }
    factory { SetLocallyMutedUseCase(get()) }
    factory { ObserveMySpeakingUseCase(get()) }
    factory { SetMicOpenUseCase(get()) }
    factory { ObserveMicOnUseCase(get()) }
    factory { SetMicOnUseCase(get()) }
}
