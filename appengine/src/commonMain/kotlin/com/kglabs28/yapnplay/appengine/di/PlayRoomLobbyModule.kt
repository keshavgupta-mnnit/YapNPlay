package com.kglabs28.yapnplay.appengine.di

import com.kglabs28.yapnplay.appengine.features.playroomlobby.data.PlayRoomLobbyRepositoryImpl
import com.kglabs28.yapnplay.appengine.features.playroomlobby.data.remote.LobbyRealtimeClient
import com.kglabs28.yapnplay.appengine.features.playroomlobby.data.remote.LobbyRealtimeClientImpl
import com.kglabs28.yapnplay.appengine.features.playroomlobby.repository.PlayRoomLobbyRepository
import com.kglabs28.yapnplay.appengine.features.playroomlobby.usecase.ObserveMatchStartingUseCase
import com.kglabs28.yapnplay.appengine.features.playroomlobby.usecase.ObservePlayRoomLobbyUseCase
import com.kglabs28.yapnplay.appengine.features.playroomlobby.usecase.SendLobbyCommandUseCase
import com.kglabs28.yapnplay.appengine.ui.screens.playroomlobby.PlayRoomLobbyViewModel
import org.koin.dsl.module

/** Play-room lobby: fake server today, WebSocket room channel later. */
val playRoomLobbyModule = module {
    single<LobbyRealtimeClient> { LobbyRealtimeClientImpl(get(), get(), get(), get()) }
    single<PlayRoomLobbyRepository> { PlayRoomLobbyRepositoryImpl(get(), get(), get()) }
    factory { ObservePlayRoomLobbyUseCase(get(), get(), get()) }
    factory { SendLobbyCommandUseCase(get()) }
    factory { ObserveMatchStartingUseCase(get()) }
    factory { PlayRoomLobbyViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get()) }
}
