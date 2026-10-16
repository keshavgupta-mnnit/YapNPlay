package com.kglabs28.yapnplay.appengine.di

import com.kglabs28.yapnplay.appengine.appmanager.AppManager
import org.koin.dsl.module

/**
 * AppManager itself. The [AppConfigStore] comes from the platform module
 * (SharedPreferences on Android, in-memory on JVM) — construction loads
 * local values instantly, so the app never waits on the network.
 */
val appManagerModule = module {
    single { AppManager(get()) }
}
