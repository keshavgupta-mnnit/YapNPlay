package com.kglabs28.yapnplay.appengine.di

import com.kglabs28.yapnplay.appengine.appmanager.AndroidConfigStore
import com.kglabs28.yapnplay.appengine.appmanager.AppConfigStore
import com.kglabs28.yapnplay.appengine.features.notifications.data.local.AndroidNotificationHistoryStore
import com.kglabs28.yapnplay.appengine.features.notifications.data.local.NotificationHistoryStore
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/** Android local stores (SharedPreferences): appmanager config, notification history. */
val platformAppManagerModule = module {
    single<AppConfigStore> { AndroidConfigStore(androidContext()) }
    single<NotificationHistoryStore> { AndroidNotificationHistoryStore(androidContext()) }
}
