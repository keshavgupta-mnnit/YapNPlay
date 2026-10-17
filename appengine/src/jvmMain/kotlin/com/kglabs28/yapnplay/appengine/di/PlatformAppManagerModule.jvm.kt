package com.kglabs28.yapnplay.appengine.di

import com.kglabs28.yapnplay.appengine.appmanager.AppConfigStore
import com.kglabs28.yapnplay.appengine.appmanager.JvmConfigStore
import com.kglabs28.yapnplay.appengine.features.notifications.data.local.InMemoryNotificationHistoryStore
import com.kglabs28.yapnplay.appengine.features.notifications.data.local.NotificationHistoryStore
import org.koin.dsl.module

/** JVM local stores (in memory): appmanager config, notification history. */
val platformAppManagerModule = module {
    single<AppConfigStore> { JvmConfigStore() }
    single<NotificationHistoryStore> { InMemoryNotificationHistoryStore() }
}
