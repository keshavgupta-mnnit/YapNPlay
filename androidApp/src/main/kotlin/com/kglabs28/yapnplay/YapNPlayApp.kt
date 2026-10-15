package com.kglabs28.yapnplay

import android.app.Application
import com.kglabs28.yapnplay.appengine.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

/**
 * Thin launch shell: starts Koin with the shared [appModules].
 * All UI and logic live in :appengine.
 */
class YapNPlayApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@YapNPlayApp)
            modules(appModules)
        }
    }
}
