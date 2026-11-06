package com.kglabs28.yapnplay.appengine.di

import com.kglabs28.yapnplay.appengine.games.cyberblast.data.CyberBlastMatchRepositoryImpl
import com.kglabs28.yapnplay.appengine.games.cyberblast.data.remote.CyberBlastMatchClient
import com.kglabs28.yapnplay.appengine.games.cyberblast.data.remote.CyberBlastSnapshotMapper
import com.kglabs28.yapnplay.appengine.games.cyberblast.domain.CyberBlastMatchRepository
import com.kglabs28.yapnplay.appengine.games.cyberblast.domain.usecase.ObserveCyberFramesUseCase
import com.kglabs28.yapnplay.appengine.games.cyberblast.domain.usecase.ObserveCyberMatchUseCase
import com.kglabs28.yapnplay.appengine.games.cyberblast.domain.usecase.ObserveCyberSpeakingUseCase
import com.kglabs28.yapnplay.appengine.games.cyberblast.domain.usecase.SendCyberCommandUseCase
import com.kglabs28.yapnplay.appengine.games.cyberblast.ui.screens.match.CyberBlastMatchViewModel
import org.koin.dsl.module

/** Cyber Blast's table over the WebSocket match channel (its live frames too). */
val cyberBlastModule = module {
    single { CyberBlastSnapshotMapper(get()) }
    single { CyberBlastMatchClient(get(), get(), get(), get(), get()) }
    single<CyberBlastMatchRepository> { CyberBlastMatchRepositoryImpl(get(), get()) }
    factory { ObserveCyberMatchUseCase(get()) }
    factory { ObserveCyberFramesUseCase(get()) }
    factory { ObserveCyberSpeakingUseCase(get()) }
    factory { SendCyberCommandUseCase(get()) }
    factory { CyberBlastMatchViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }
}
