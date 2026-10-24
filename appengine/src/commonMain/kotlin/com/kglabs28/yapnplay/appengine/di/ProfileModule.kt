package com.kglabs28.yapnplay.appengine.di

import com.kglabs28.yapnplay.appengine.features.profile.usecase.GetPlayGamesNameUseCase
import com.kglabs28.yapnplay.appengine.ui.screens.welcome.WelcomeViewModel
import com.kglabs28.yapnplay.appengine.features.profile.usecase.FinishWelcomeUseCase
import com.kglabs28.yapnplay.appengine.features.profile.usecase.ObserveWelcomeNeededUseCase
import com.kglabs28.yapnplay.appengine.features.profile.usecase.FriendInvites
import com.kglabs28.yapnplay.appengine.features.profile.usecase.AnswerDeviceAccountOfferUseCase
import com.kglabs28.yapnplay.appengine.features.profile.usecase.ObserveDeviceAccountOfferUseCase
import com.kglabs28.yapnplay.appengine.features.profile.usecase.CollectDailyBonusUseCase
import com.kglabs28.yapnplay.appengine.features.profile.usecase.ObserveDailyBonusUseCase
import com.kglabs28.yapnplay.appengine.features.profile.data.ProfileRepositoryImpl
import com.kglabs28.yapnplay.appengine.features.profile.data.local.ProfileLocalManager
import com.kglabs28.yapnplay.appengine.features.profile.data.remote.ProfileApi
import com.kglabs28.yapnplay.appengine.features.profile.data.remote.ProfileApiClient
import com.kglabs28.yapnplay.appengine.features.profile.data.remote.ProfileRemoteManager
import com.kglabs28.yapnplay.appengine.features.profile.repository.ProfileRepository
import com.kglabs28.yapnplay.appengine.features.profile.usecase.BlockUserUseCase
import com.kglabs28.yapnplay.appengine.features.profile.usecase.GetFriendIdsUseCase
import com.kglabs28.yapnplay.appengine.features.profile.usecase.SignInWithPlayGamesUseCase
import com.kglabs28.yapnplay.appengine.features.profile.usecase.ObserveSignInNeededUseCase
import com.kglabs28.yapnplay.appengine.features.profile.usecase.GetFriendsUseCase
import com.kglabs28.yapnplay.appengine.features.profile.usecase.PlayerFriendUseCases
import com.kglabs28.yapnplay.appengine.features.profile.usecase.TogglePlayerFriendUseCase
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
 * Profile bindings: the backend's HTTP API behind the remote manager,
 * cached by the local manager, behind the repository.
 */
val profileModule = module {
    single<ProfileApi> { ProfileApiClient(get()) }
    single { ProfileRemoteManager(get()) }
    single { ProfileLocalManager() }
    single<ProfileRepository> { ProfileRepositoryImpl(get(), get()) }
    factory { GetHomeScreenDataUseCase(get()) }
    factory { GetProfileScreenDataUseCase(get()) }
    factory { ObserveMyProfileUseCase(get()) }
    factory { ObserveDailyBonusUseCase(get()) }
    factory { CollectDailyBonusUseCase(get()) }
    factory { UpdateProfileUseCase(get()) }
    factory { ObserveWelcomeNeededUseCase(get()) }
    factory { FinishWelcomeUseCase(get()) }
    factory { GetPlayGamesNameUseCase(get()) }
    factory { WelcomeViewModel(get(), get(), get(), get(), get(), get(), get()) }
    factory { SendFriendRequestUseCase(get()) }
    factory { RespondToFriendRequestUseCase(get()) }
    factory { RemoveFriendUseCase(get()) }
    factory { BlockUserUseCase(get()) }
    factory { UnblockUserUseCase(get()) }
    factory { GetFriendsUseCase(get()) }
    factory { GetFriendIdsUseCase(get()) }
    factory { SignInWithPlayGamesUseCase(get()) }
    factory { ObserveSignInNeededUseCase(get()) }
    factory { ObserveDeviceAccountOfferUseCase(get()) }
    factory { AnswerDeviceAccountOfferUseCase(get()) }
    factory { TogglePlayerFriendUseCase(get()) }
    factory { PlayerFriendUseCases(get(), get()) }
    factory { ProfileViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }
    factory { FriendInvites(get()) }
}
