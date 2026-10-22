package com.kglabs28.yapnplay.appengine.di

import com.kglabs28.yapnplay.appengine.games.monochaos.data.MonoChaosMatchRepositoryImpl
import com.kglabs28.yapnplay.appengine.games.monochaos.data.remote.MonoChaosMatchClient
import com.kglabs28.yapnplay.appengine.games.monochaos.data.remote.MonoChaosMatchClientImpl
import com.kglabs28.yapnplay.appengine.games.monochaos.data.remote.MonoChaosSnapshotMapper
import com.kglabs28.yapnplay.appengine.games.monochaos.domain.MonoChaosMatchRepository
import com.kglabs28.yapnplay.appengine.games.monochaos.domain.usecase.ObserveMonoChaosMatchUseCase
import com.kglabs28.yapnplay.appengine.games.monochaos.domain.usecase.ObserveMonoChaosSpeakingUseCase
import com.kglabs28.yapnplay.appengine.games.monochaos.domain.usecase.SendMonoChaosCommandUseCase
import com.kglabs28.yapnplay.appengine.ui.screens.mono.MatchMonoChaosViewModel
import org.koin.dsl.module

/**
 * MONO: Complete Chaos: repository over the MATCH channel, its use cases
 * and the match screen's view model. The shared
 * [kotlinx.serialization.json.Json] and the match channel come from
 * [coreModule].
 */
val monoChaosModule = module {
    single { MonoChaosSnapshotMapper(get()) }
    single<MonoChaosMatchClient> { MonoChaosMatchClientImpl(get(), get(), get(), get(), get(), get()) }
    single<MonoChaosMatchRepository> { MonoChaosMatchRepositoryImpl(get(), get()) }
    factory { ObserveMonoChaosMatchUseCase(get()) }
    factory { SendMonoChaosCommandUseCase(get()) }
    factory { ObserveMonoChaosSpeakingUseCase(get()) }
    factory { MatchMonoChaosViewModel(get(), get(), get(), get(), get(), get(), get()) }
}