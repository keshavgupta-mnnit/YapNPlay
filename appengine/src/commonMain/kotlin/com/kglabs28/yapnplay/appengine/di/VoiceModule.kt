package com.kglabs28.yapnplay.appengine.di

import com.kglabs28.yapnplay.appengine.features.voice.usecase.ObserveMutedTalkersUseCase
import com.kglabs28.yapnplay.appengine.features.voice.data.MutedTalkReporter
import com.kglabs28.yapnplay.appengine.features.voice.data.VoiceRepositoryImpl
import com.kglabs28.yapnplay.appengine.features.voice.data.remote.VoiceClient
import com.kglabs28.yapnplay.appengine.features.voice.data.remote.VoiceClientImpl
import com.kglabs28.yapnplay.appengine.features.voice.data.remote.VoiceTokenApi
import com.kglabs28.yapnplay.appengine.features.voice.domain.VoiceRepository
import com.kglabs28.yapnplay.appengine.features.voice.usecase.ObserveLocallyMutedUseCase
import com.kglabs28.yapnplay.appengine.features.voice.usecase.ObserveMicOnUseCase
import com.kglabs28.yapnplay.appengine.features.voice.usecase.ObserveMySpeakingUseCase
import com.kglabs28.yapnplay.appengine.features.voice.usecase.SetLocallyMutedUseCase
import com.kglabs28.yapnplay.appengine.features.voice.usecase.SetMicOnUseCase
import com.kglabs28.yapnplay.appengine.features.voice.usecase.ObserveSpeakerOnUseCase
import com.kglabs28.yapnplay.appengine.features.voice.usecase.SetSpeakerOnUseCase
import com.kglabs28.yapnplay.appengine.features.voice.usecase.TableVoiceUseCases
import com.kglabs28.yapnplay.appengine.features.voice.usecase.SetMicOpenUseCase
import com.kglabs28.yapnplay.appengine.features.voice.usecase.SetVoiceWantedUseCase
import org.koin.dsl.module

/** Voice controls shared by every game and room: local mute, and the user's own mic. */
val voiceModule = module {
    single { VoiceTokenApi(get()) }
    // VoiceEngine (LiveKit on Android) comes from platformAppManagerModule.
    single<VoiceClient> { VoiceClientImpl(get(), get(), get(), get(), get(APP_SCOPE)) }
    single<VoiceRepository> { VoiceRepositoryImpl(get()) }
    // Tells the lobby or table on screen when the user talks with the mic off (others see a grey glow).
    single(createdAtStart = true) { MutedTalkReporter(get(), get(), get(), get(APP_SCOPE)) }
    factory { ObserveMutedTalkersUseCase(get()) }
    factory { ObserveLocallyMutedUseCase(get()) }
    factory { SetLocallyMutedUseCase(get()) }
    factory { ObserveMySpeakingUseCase(get()) }
    factory { SetMicOpenUseCase(get()) }
    factory { ObserveMicOnUseCase(get()) }
    factory { SetMicOnUseCase(get()) }
    factory { ObserveSpeakerOnUseCase(get()) }
    factory { SetSpeakerOnUseCase(get()) }
    factory { SetVoiceWantedUseCase(get()) }
    factory { TableVoiceUseCases(get(), get(), get(), get(), get(), get()) }
}
