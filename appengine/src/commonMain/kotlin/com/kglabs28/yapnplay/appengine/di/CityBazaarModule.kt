package com.kglabs28.yapnplay.appengine.di

import com.kglabs28.yapnplay.appengine.games.citybazaar.data.CityBazaarMatchRepositoryImpl
import com.kglabs28.yapnplay.appengine.games.citybazaar.data.remote.CityBazaarMatchClient
import com.kglabs28.yapnplay.appengine.games.citybazaar.data.remote.CityBazaarMatchClientImpl
import com.kglabs28.yapnplay.appengine.games.citybazaar.data.remote.CityBazaarSnapshotMapper
import com.kglabs28.yapnplay.appengine.games.citybazaar.domain.CityBazaarMatchRepository
import com.kglabs28.yapnplay.appengine.games.citybazaar.domain.usecase.ObserveCityBazaarMatchUseCase
import com.kglabs28.yapnplay.appengine.games.citybazaar.domain.usecase.ObserveCityBazaarSpeakingUseCase
import com.kglabs28.yapnplay.appengine.games.citybazaar.domain.usecase.SendCityBazaarCommandUseCase
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CityBazaarMatchViewModel
import org.koin.dsl.module

/** City Bazaar table over the WebSocket match channel. */
val cityBazaarModule = module {
    single { CityBazaarSnapshotMapper(get()) }
    single<CityBazaarMatchClient> { CityBazaarMatchClientImpl(get(), get(), get(), get(), get(), get()) }
    single<CityBazaarMatchRepository> { CityBazaarMatchRepositoryImpl(get(), get()) }
    factory { ObserveCityBazaarMatchUseCase(get()) }
    factory { ObserveCityBazaarSpeakingUseCase(get()) }
    factory { SendCityBazaarCommandUseCase(get()) }
    factory { CityBazaarMatchViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }
}
