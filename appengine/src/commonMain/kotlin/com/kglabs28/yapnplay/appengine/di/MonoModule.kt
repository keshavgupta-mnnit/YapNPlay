package com.kglabs28.yapnplay.appengine.di

import com.kglabs28.yapnplay.appengine.games.mono.data.MonoMatchRepositoryImpl
import com.kglabs28.yapnplay.appengine.games.mono.data.remote.MonoMatchClient
import com.kglabs28.yapnplay.appengine.games.mono.data.remote.MonoMatchClientImpl
import com.kglabs28.yapnplay.appengine.games.mono.data.remote.MonoSnapshotMapper
import com.kglabs28.yapnplay.appengine.games.mono.domain.MonoMatchRepository
import com.kglabs28.yapnplay.appengine.games.mono.domain.usecase.ObserveMonoMatchUseCase
import com.kglabs28.yapnplay.appengine.games.mono.domain.usecase.ObserveMonoSpeakingUseCase
import com.kglabs28.yapnplay.appengine.games.mono.domain.usecase.SendMonoCommandUseCase
import com.kglabs28.yapnplay.appengine.ui.screens.mono.MatchMonoViewModel
import org.koin.dsl.module

/**
 * Standard MONO: repository over the MATCH channel, its use cases and the
 * match screen's view model. The shared [kotlinx.serialization.json.Json]
 * and the match channel come from [coreModule].
 */
val monoModule = module {
    single { MonoSnapshotMapper(get()) }
    single<MonoMatchClient> { MonoMatchClientImpl(get(), get(), get(), get(), get(), get()) }
    single<MonoMatchRepository> { MonoMatchRepositoryImpl(get(), get()) }
    factory { ObserveMonoMatchUseCase(get()) }
    factory { SendMonoCommandUseCase(get()) }
    factory { ObserveMonoSpeakingUseCase(get()) }
    factory { MatchMonoViewModel(get(), get(), get(), get(), get(), get(), get(), get()) }
}