package com.kglabs28.yapnplay.appengine.di

import com.kglabs28.yapnplay.appengine.appmanager.AppConfigStore
import com.kglabs28.yapnplay.appengine.appmanager.JvmConfigStore
import org.koin.dsl.module

/** JVM backing for the appmanager: in-memory local store. */
val platformAppManagerModule = module {
    single<AppConfigStore> { JvmConfigStore() }
}
