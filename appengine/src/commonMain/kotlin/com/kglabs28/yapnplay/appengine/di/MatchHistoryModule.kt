package com.kglabs28.yapnplay.appengine.di

import com.kglabs28.yapnplay.appengine.features.matchhistory.data.MatchHistoryRepositoryImpl
import com.kglabs28.yapnplay.appengine.features.matchhistory.domain.MatchHistoryRepository
import com.kglabs28.yapnplay.appengine.features.matchhistory.usecase.GetMatchHistoryUseCase
import com.kglabs28.yapnplay.appengine.ui.screens.history.MatchHistoryViewModel
import org.koin.dsl.module

/** Match history: the user's past matches and their leaderboards. */
val matchHistoryModule = module {
    single<MatchHistoryRepository> { MatchHistoryRepositoryImpl(get(), get()) }
    factory { GetMatchHistoryUseCase(get()) }
    factory { MatchHistoryViewModel(get(), get()) }
}
