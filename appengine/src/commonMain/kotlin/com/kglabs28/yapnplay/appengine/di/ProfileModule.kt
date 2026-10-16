package com.kglabs28.yapnplay.appengine.di

import com.kglabs28.yapnplay.appengine.features.profile.data.ProfileRepositoryImpl
import com.kglabs28.yapnplay.appengine.features.profile.data.local.ProfileLocalManager
import com.kglabs28.yapnplay.appengine.features.profile.data.remote.ProfileApi
import com.kglabs28.yapnplay.appengine.features.profile.data.remote.ProfileApiClient
import com.kglabs28.yapnplay.appengine.features.profile.data.remote.ProfileRemoteManager
import com.kglabs28.yapnplay.appengine.features.profile.repository.ProfileRepository
import com.kglabs28.yapnplay.appengine.features.profile.usecase.GetHomeScreenData
import org.koin.dsl.module

/**
 * Profile bindings: fake-backed client today, real HTTP tomorrow by
 * swapping one line. Managers and repository stay untouched.
 */
val profileModule = module {
    single<ProfileApi> { ProfileApiClient(get()) }
    single { ProfileRemoteManager(get()) }
    single { ProfileLocalManager() }
    single<ProfileRepository> { ProfileRepositoryImpl(get(), get()) }
    factory { GetHomeScreenData(get()) }
}
