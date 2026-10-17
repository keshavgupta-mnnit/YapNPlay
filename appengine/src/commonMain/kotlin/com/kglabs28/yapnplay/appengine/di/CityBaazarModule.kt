package com.kglabs28.yapnplay.appengine.di

import com.kglabs28.yapnplay.appengine.games.citybaazar.data.CityBaazarMatchRepositoryImpl
import com.kglabs28.yapnplay.appengine.games.citybaazar.data.remote.CityBaazarMatchClient
import com.kglabs28.yapnplay.appengine.games.citybaazar.data.remote.CityBaazarMatchClientImpl
import com.kglabs28.yapnplay.appengine.games.citybaazar.data.remote.FakeCityBaazarServer
import com.kglabs28.yapnplay.appengine.games.citybaazar.domain.CityBaazarMatchRepository
import com.kglabs28.yapnplay.appengine.games.citybaazar.domain.usecase.ObserveCityBaazarMatchUseCase
import com.kglabs28.yapnplay.appengine.games.citybaazar.domain.usecase.ObserveCityBaazarSpeakingUseCase
import com.kglabs28.yapnplay.appengine.games.citybaazar.domain.usecase.SendCityBaazarCommandUseCase
import com.kglabs28.yapnplay.appengine.games.citybaazar.ui.screens.match.CityBaazarMatchViewModel
import org.koin.dsl.module

/** City Baazar table: fake match server today, WebSocket match channel later. */
val cityBaazarModule = module {
    single { FakeCityBaazarServer(get(), get()) }
    single<CityBaazarMatchClient> { CityBaazarMatchClientImpl(get()) }
    single<CityBaazarMatchRepository> { CityBaazarMatchRepositoryImpl(get(), get()) }
    factory { ObserveCityBaazarMatchUseCase(get()) }
    factory { ObserveCityBaazarSpeakingUseCase(get()) }
    factory { SendCityBaazarCommandUseCase(get()) }
    factory { CityBaazarMatchViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get()) }
}
