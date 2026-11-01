package com.kglabs28.yapnplay.appengine.di

import com.kglabs28.yapnplay.appengine.games.doodleking.data.DoodleKingMatchRepositoryImpl
import com.kglabs28.yapnplay.appengine.games.doodleking.data.remote.DoodleKingMatchClient
import com.kglabs28.yapnplay.appengine.games.doodleking.data.remote.DoodleKingSnapshotMapper
import com.kglabs28.yapnplay.appengine.games.doodleking.domain.DoodleKingMatchRepository
import com.kglabs28.yapnplay.appengine.games.doodleking.domain.usecase.ObserveDoodleInkUseCase
import com.kglabs28.yapnplay.appengine.games.doodleking.domain.usecase.ObserveDoodleMatchUseCase
import com.kglabs28.yapnplay.appengine.games.doodleking.domain.usecase.ObserveDoodleSpeakingUseCase
import com.kglabs28.yapnplay.appengine.games.doodleking.domain.usecase.SendDoodleCommandUseCase
import com.kglabs28.yapnplay.appengine.games.doodleking.domain.usecase.SendDoodleInkUseCase
import com.kglabs28.yapnplay.appengine.games.doodleking.ui.screens.match.DoodleKingMatchViewModel
import org.koin.dsl.module

/** Doodle King's table over the WebSocket match channel (its drawing too). */
val doodleKingModule = module {
    single { DoodleKingSnapshotMapper(get()) }
    single { DoodleKingMatchClient(get(), get(), get(), get(), get(), get()) }
    single<DoodleKingMatchRepository> { DoodleKingMatchRepositoryImpl(get(), get()) }
    factory { ObserveDoodleMatchUseCase(get()) }
    factory { ObserveDoodleInkUseCase(get()) }
    factory { ObserveDoodleSpeakingUseCase(get()) }
    factory { SendDoodleCommandUseCase(get()) }
    factory { SendDoodleInkUseCase(get()) }
    factory { DoodleKingMatchViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }
}
