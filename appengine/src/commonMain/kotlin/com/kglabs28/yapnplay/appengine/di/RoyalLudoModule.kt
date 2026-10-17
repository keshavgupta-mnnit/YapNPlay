package com.kglabs28.yapnplay.appengine.di

import com.kglabs28.yapnplay.appengine.games.royalludo.data.LudoMatchRepositoryImpl
import com.kglabs28.yapnplay.appengine.games.royalludo.data.remote.FakeLudoMatchServer
import com.kglabs28.yapnplay.appengine.games.royalludo.data.remote.LudoMatchClient
import com.kglabs28.yapnplay.appengine.games.royalludo.data.remote.LudoMatchClientImpl
import com.kglabs28.yapnplay.appengine.games.royalludo.domain.LudoMatchRepository
import com.kglabs28.yapnplay.appengine.games.royalludo.domain.usecase.ObserveLudoMatchUseCase
import com.kglabs28.yapnplay.appengine.games.royalludo.domain.usecase.ObserveMatchSpeakingUseCase
import com.kglabs28.yapnplay.appengine.games.royalludo.domain.usecase.SendLudoCommandUseCase
import com.kglabs28.yapnplay.appengine.games.royalludo.ui.screens.match.RoyalLudoMatchViewModel
import org.koin.dsl.module

/** Royal Ludo table: fake match server today, WebSocket match channel later. */
val royalLudoModule = module {
    single { FakeLudoMatchServer(get(), get()) }
    single<LudoMatchClient> { LudoMatchClientImpl(get()) }
    single<LudoMatchRepository> { LudoMatchRepositoryImpl(get(), get()) }
    factory { ObserveLudoMatchUseCase(get()) }
    factory { ObserveMatchSpeakingUseCase(get()) }
    factory { SendLudoCommandUseCase(get()) }
    factory { RoyalLudoMatchViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get()) }
}
