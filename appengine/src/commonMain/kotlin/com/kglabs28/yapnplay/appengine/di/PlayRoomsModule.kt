package com.kglabs28.yapnplay.appengine.di

import com.kglabs28.yapnplay.appengine.features.playrooms.data.PlayRoomsRepositoryImpl
import com.kglabs28.yapnplay.appengine.features.playrooms.data.local.PlayRoomsLocalManager
import com.kglabs28.yapnplay.appengine.features.playrooms.data.remote.PlayRoomsApi
import com.kglabs28.yapnplay.appengine.features.playrooms.data.remote.PlayRoomsApiClient
import com.kglabs28.yapnplay.appengine.features.playrooms.data.remote.PlayRoomsRemoteManager
import com.kglabs28.yapnplay.appengine.features.playrooms.repository.PlayRoomsRepository
import com.kglabs28.yapnplay.appengine.features.playrooms.usecase.GetPlayRoomsScreenDataUseCase
import com.kglabs28.yapnplay.appengine.features.playrooms.usecase.JoinPlayRoomByCodeUseCase
import org.koin.dsl.module

/**
 * Rooms bindings: fake-backed client today, real HTTP tomorrow by
 * uncommenting the client calls. Managers and repository stay untouched.
 */
val playRoomsModule = module {
    single<PlayRoomsApi> { PlayRoomsApiClient(get()) }
    single { PlayRoomsRemoteManager(get()) }
    single { PlayRoomsLocalManager() }
    single<PlayRoomsRepository> { PlayRoomsRepositoryImpl(get(), get(), get()) }
    factory { GetPlayRoomsScreenDataUseCase(get(), get()) }
    factory { JoinPlayRoomByCodeUseCase(get()) }
}
