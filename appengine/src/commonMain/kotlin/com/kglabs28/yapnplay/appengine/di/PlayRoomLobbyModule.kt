package com.kglabs28.yapnplay.appengine.di

import com.kglabs28.yapnplay.appengine.features.playroomlobby.usecase.GetRoomPendingInvitesUseCase
import com.kglabs28.yapnplay.appengine.features.playroomlobby.data.RoomPendingInvitesRepositoryImpl
import com.kglabs28.yapnplay.appengine.features.playroomlobby.repository.RoomPendingInvitesRepository
import com.kglabs28.yapnplay.appengine.features.playroomlobby.usecase.GetRoomLeaderboardUseCase
import com.kglabs28.yapnplay.appengine.features.playroomlobby.data.RoomLeaderboardRepositoryImpl
import com.kglabs28.yapnplay.appengine.features.playroomlobby.repository.RoomLeaderboardRepository
import com.kglabs28.yapnplay.appengine.features.playroomlobby.data.PlayRoomLobbyRepositoryImpl
import com.kglabs28.yapnplay.appengine.features.playroomlobby.data.remote.LobbyRealtimeClient
import com.kglabs28.yapnplay.appengine.features.playroomlobby.data.remote.LobbyRealtimeClientImpl
import com.kglabs28.yapnplay.appengine.features.playroomlobby.repository.PlayRoomLobbyRepository
import com.kglabs28.yapnplay.appengine.features.playroomlobby.usecase.ObserveMatchStartingUseCase
import com.kglabs28.yapnplay.appengine.features.playroomlobby.usecase.ObservePlayRoomLobbyUseCase
import com.kglabs28.yapnplay.appengine.features.playroomlobby.usecase.SendLobbyCommandUseCase
import com.kglabs28.yapnplay.appengine.ui.screens.playroomlobby.PlayRoomLobbyViewModel
import org.koin.dsl.module

/** Play-room lobby over the backend's ROOM WebSocket channel. */
val playRoomLobbyModule = module {
    single<LobbyRealtimeClient> { LobbyRealtimeClientImpl(get(), get(), get(), get()) }
    single<PlayRoomLobbyRepository> { PlayRoomLobbyRepositoryImpl(get(), get(), get()) }
    single<RoomLeaderboardRepository> { RoomLeaderboardRepositoryImpl(get(), get()) }
    factory { GetRoomLeaderboardUseCase(get()) }
    single<RoomPendingInvitesRepository> { RoomPendingInvitesRepositoryImpl(get(), get()) }
    factory { GetRoomPendingInvitesUseCase(get()) }
    factory { ObservePlayRoomLobbyUseCase(get(), get(), get()) }
    factory { SendLobbyCommandUseCase(get()) }
    factory { ObserveMatchStartingUseCase(get()) }
    factory { PlayRoomLobbyViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }
}
