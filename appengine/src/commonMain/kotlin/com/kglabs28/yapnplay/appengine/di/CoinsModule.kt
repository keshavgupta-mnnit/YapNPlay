package com.kglabs28.yapnplay.appengine.di

import com.kglabs28.yapnplay.appengine.features.coins.data.CoinsRepositoryImpl
import com.kglabs28.yapnplay.appengine.features.coins.data.remote.CoinsClient
import com.kglabs28.yapnplay.appengine.features.coins.data.remote.CoinsClientImpl
import com.kglabs28.yapnplay.appengine.features.coins.domain.CoinsRepository
import com.kglabs28.yapnplay.appengine.features.coins.usecase.GetCoinLeaguesUseCase
import com.kglabs28.yapnplay.appengine.features.coins.usecase.WatchAdForCoinsUseCase
import org.koin.dsl.module

/** Coins: leagues and rewarded-ad coins (RewardedAds comes from platformAppManagerModule). */
val coinsModule = module {
    single<CoinsClient> { CoinsClientImpl(get()) }
    single<CoinsRepository> { CoinsRepositoryImpl(get(), get()) }
    factory { GetCoinLeaguesUseCase(get()) }
    factory { WatchAdForCoinsUseCase(get(), get()) }
}
