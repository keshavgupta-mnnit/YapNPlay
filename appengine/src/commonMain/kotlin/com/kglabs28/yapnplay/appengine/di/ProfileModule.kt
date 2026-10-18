package com.kglabs28.yapnplay.appengine.di

import com.kglabs28.yapnplay.appengine.features.profile.data.ProfileRepositoryImpl
import com.kglabs28.yapnplay.appengine.features.profile.data.local.ProfileLocalManager
import com.kglabs28.yapnplay.appengine.features.profile.data.remote.FakeProfileServer
import com.kglabs28.yapnplay.appengine.features.profile.data.remote.ProfileApi
import com.kglabs28.yapnplay.appengine.features.profile.data.remote.ProfileApiClient
import com.kglabs28.yapnplay.appengine.features.profile.data.remote.ProfileRemoteManager
import com.kglabs28.yapnplay.appengine.features.profile.repository.ProfileRepository
import com.kglabs28.yapnplay.appengine.features.profile.usecase.BlockUserUseCase
import com.kglabs28.yapnplay.appengine.features.profile.usecase.GetHomeScreenDataUseCase
import com.kglabs28.yapnplay.appengine.features.profile.usecase.GetProfileScreenDataUseCase
import com.kglabs28.yapnplay.appengine.features.profile.usecase.ObserveMyProfileUseCase
import com.kglabs28.yapnplay.appengine.features.profile.usecase.RemoveFriendUseCase
import com.kglabs28.yapnplay.appengine.features.profile.usecase.RespondToFriendRequestUseCase
import com.kglabs28.yapnplay.appengine.features.profile.usecase.SendFriendRequestUseCase
import com.kglabs28.yapnplay.appengine.features.profile.usecase.UnblockUserUseCase
import com.kglabs28.yapnplay.appengine.features.profile.usecase.UpdateProfileUseCase
import com.kglabs28.yapnplay.appengine.ui.screens.profile.ProfileViewModel
import org.koin.dsl.module

/**
 * Profile bindings: fake-backed client today, real HTTP tomorrow by
 * swapping one line. Managers and repository stay untouched.
 */
val profileModule = module {
    single { FakeProfileServer(get()) }
    single<ProfileApi> { ProfileApiClient(get(), get()) }
    single { ProfileRemoteManager(get()) }
    single { ProfileLocalManager() }
    single<ProfileRepository> { ProfileRepositoryImpl(get(), get()) }
    factory { GetHomeScreenDataUseCase(get()) }
    factory { GetProfileScreenDataUseCase(get()) }
    factory { ObserveMyProfileUseCase(get()) }
    factory { UpdateProfileUseCase(get()) }
    factory { SendFriendRequestUseCase(get()) }
    factory { RespondToFriendRequestUseCase(get()) }
    factory { RemoveFriendUseCase(get()) }
    factory { BlockUserUseCase(get()) }
    factory { UnblockUserUseCase(get()) }
    factory { ProfileViewModel(get(), get(), get(), get(), get(), get(), get(), get()) }
}
