package com.kglabs28.yapnplay.appengine.di

import com.kglabs28.yapnplay.appengine.games.royalludo.data.LudoMatchRepositoryImpl
import com.kglabs28.yapnplay.appengine.games.royalludo.data.remote.LudoMatchClient
import com.kglabs28.yapnplay.appengine.games.royalludo.data.remote.LudoMatchClientImpl
import com.kglabs28.yapnplay.appengine.games.royalludo.data.remote.LudoSnapshotMapper
import com.kglabs28.yapnplay.appengine.games.royalludo.domain.LudoMatchRepository
import com.kglabs28.yapnplay.appengine.games.royalludo.domain.usecase.ObserveLudoMatchUseCase
import com.kglabs28.yapnplay.appengine.games.royalludo.domain.usecase.ObserveMatchSpeakingUseCase
import com.kglabs28.yapnplay.appengine.games.royalludo.domain.usecase.SendLudoCommandUseCase
import com.kglabs28.yapnplay.appengine.games.royalludo.ui.screens.match.RoyalLudoMatchViewModel
import org.koin.dsl.module

/** Royal Ludo table over the WebSocket match channel. */
val royalLudoModule = module {
    single { LudoSnapshotMapper(get()) }
    single<LudoMatchClient> { LudoMatchClientImpl(get(), get(), get(), get(), get()) }
    single<LudoMatchRepository> { LudoMatchRepositoryImpl(get(), get()) }
    factory { ObserveLudoMatchUseCase(get()) }
    factory { ObserveMatchSpeakingUseCase(get()) }
    factory { SendLudoCommandUseCase(get()) }
    factory { RoyalLudoMatchViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }
}
