package com.kglabs28.yapnplay.appengine.di

import com.kglabs28.yapnplay.appengine.features.notifications.data.DevicesRepositoryImpl
import com.kglabs28.yapnplay.appengine.features.notifications.data.remote.DevicesApi
import com.kglabs28.yapnplay.appengine.features.notifications.data.remote.DevicesApiClient
import com.kglabs28.yapnplay.appengine.features.notifications.repository.DevicesRepository
import com.kglabs28.yapnplay.appengine.features.notifications.data.NotificationsRepositoryImpl
import com.kglabs28.yapnplay.appengine.features.notifications.data.local.NotificationsLocalManager
import com.kglabs28.yapnplay.appengine.features.notifications.repository.NotificationsRepository
import com.kglabs28.yapnplay.appengine.features.notifications.usecase.MarkNotificationsReadUseCase
import com.kglabs28.yapnplay.appengine.features.notifications.usecase.ObserveNotificationsUseCase
import com.kglabs28.yapnplay.appengine.features.notifications.usecase.RegisterPushTokenUseCase
import com.kglabs28.yapnplay.appengine.features.notifications.usecase.SaveNotificationUseCase
import org.koin.dsl.module

/**
 * Push notifications: device registration and the bell's history
 * (NotificationHistoryStore comes from the platform module; display
 * lives in the Android shell).
 */
val notificationsModule = module {
    single<DevicesApi> { DevicesApiClient(get()) }
    single<DevicesRepository> { DevicesRepositoryImpl(get(), get()) }
    factory { RegisterPushTokenUseCase(get()) }

    single { NotificationsLocalManager(get()) }
    single<NotificationsRepository> { NotificationsRepositoryImpl(get()) }
    factory { ObserveNotificationsUseCase(get()) }
    factory { SaveNotificationUseCase(get(), get()) }
    factory { MarkNotificationsReadUseCase(get()) }
}
