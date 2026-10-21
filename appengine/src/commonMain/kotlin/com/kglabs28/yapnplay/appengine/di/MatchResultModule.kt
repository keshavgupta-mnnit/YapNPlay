package com.kglabs28.yapnplay.appengine.di

import com.kglabs28.yapnplay.appengine.features.matchresult.data.MatchResultRepositoryImpl
import com.kglabs28.yapnplay.appengine.features.matchresult.domain.MatchResultRepository
import com.kglabs28.yapnplay.appengine.features.matchresult.usecase.ObserveMatchCoinsUseCase
import org.koin.dsl.module

/** End-of-match coins, shared by every game. */
val matchResultModule = module {
    single<MatchResultRepository> { MatchResultRepositoryImpl(get()) }
    factory { ObserveMatchCoinsUseCase(get()) }
}
