package com.kglabs28.yapnplay.appengine.di

import com.kglabs28.yapnplay.appengine.games.citybazaar.data.CityBazaarMatchRepositoryImpl
import com.kglabs28.yapnplay.appengine.games.citybazaar.data.remote.CityBazaarMatchClient
import com.kglabs28.yapnplay.appengine.games.citybazaar.data.remote.CityBazaarMatchClientImpl
import com.kglabs28.yapnplay.appengine.games.citybazaar.data.remote.FakeCityBazaarServer
import com.kglabs28.yapnplay.appengine.games.citybazaar.domain.CityBazaarMatchRepository
import com.kglabs28.yapnplay.appengine.games.citybazaar.domain.usecase.ObserveCityBazaarMatchUseCase
import com.kglabs28.yapnplay.appengine.games.citybazaar.domain.usecase.ObserveCityBazaarSpeakingUseCase
import com.kglabs28.yapnplay.appengine.games.citybazaar.domain.usecase.SendCityBazaarCommandUseCase
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CityBazaarMatchViewModel
import org.koin.dsl.module

/** City Bazaar table: fake match server today, WebSocket match channel later. */
val cityBazaarModule = module {
    single { FakeCityBazaarServer(get(), get(), get()) }
    single<CityBazaarMatchClient> { CityBazaarMatchClientImpl(get()) }
    single<CityBazaarMatchRepository> { CityBazaarMatchRepositoryImpl(get(), get()) }
    factory { ObserveCityBazaarMatchUseCase(get()) }
    factory { ObserveCityBazaarSpeakingUseCase(get()) }
    factory { SendCityBazaarCommandUseCase(get()) }
    factory { CityBazaarMatchViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get()) }
}
