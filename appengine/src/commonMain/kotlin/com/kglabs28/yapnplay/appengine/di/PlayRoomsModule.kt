package com.kglabs28.yapnplay.appengine.di

import com.kglabs28.yapnplay.appengine.features.playrooms.usecase.RoomInvites
import com.kglabs28.yapnplay.appengine.features.playrooms.data.PlayRoomsRepositoryImpl
import com.kglabs28.yapnplay.appengine.features.playrooms.data.local.PlayRoomsLocalManager
import com.kglabs28.yapnplay.appengine.features.playrooms.data.remote.PlayRoomsApi
import com.kglabs28.yapnplay.appengine.features.playrooms.data.remote.PlayRoomsApiClient
import com.kglabs28.yapnplay.appengine.features.playrooms.data.remote.PlayRoomsRemoteManager
import com.kglabs28.yapnplay.appengine.features.playrooms.repository.PlayRoomsRepository
import com.kglabs28.yapnplay.appengine.features.playrooms.usecase.CreatePlayRoomUseCase
import com.kglabs28.yapnplay.appengine.features.playrooms.usecase.GetGroupInvitesUseCase
import com.kglabs28.yapnplay.appengine.features.playrooms.usecase.GetMyGroupsUseCase
import com.kglabs28.yapnplay.appengine.features.playrooms.usecase.InviteFriendsToGroupUseCase
import com.kglabs28.yapnplay.appengine.features.playrooms.usecase.RespondToGroupInviteUseCase
import com.kglabs28.yapnplay.appengine.features.playrooms.usecase.GetPlayRoomsScreenDataUseCase
import com.kglabs28.yapnplay.appengine.features.playrooms.usecase.JoinPlayRoomByCodeUseCase
import com.kglabs28.yapnplay.appengine.features.playrooms.usecase.PreviewPlayRoomUseCase
import org.koin.dsl.module

/**
 * Rooms bindings: the backend's HTTP API behind the remote manager and
 * repository.
 */
val playRoomsModule = module {
    single<PlayRoomsApi> { PlayRoomsApiClient(get(), get()) }
    single { PlayRoomsRemoteManager(get()) }
    single { PlayRoomsLocalManager() }
    single<PlayRoomsRepository> { PlayRoomsRepositoryImpl(get(), get(), get()) }
    factory { GetPlayRoomsScreenDataUseCase(get(), get()) }
    factory { JoinPlayRoomByCodeUseCase(get(), get()) }
    factory { PreviewPlayRoomUseCase(get()) }
    factory { CreatePlayRoomUseCase(get()) }
    factory { InviteFriendsToGroupUseCase(get()) }
    factory { GetGroupInvitesUseCase(get()) }
    factory { RespondToGroupInviteUseCase(get()) }
    factory { GetMyGroupsUseCase(get()) }
    factory { RoomInvites(get()) }
}
