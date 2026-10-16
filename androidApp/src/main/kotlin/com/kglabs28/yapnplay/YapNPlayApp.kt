package com.kglabs28.yapnplay

import android.app.Application
import com.kglabs28.yapnplay.appengine.appmanager.AppManager
import com.kglabs28.yapnplay.appengine.appmanager.fetchRemoteFlags
import com.kglabs28.yapnplay.appengine.di.appModules
import com.kglabs28.yapnplay.appengine.di.platformAppManagerModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin

/**
 * Thin launch shell: starts Koin with the shared [appModules] plus the
 * Android appmanager backing, then refreshes remote flags in the
 * background. Local values apply instantly; remote merges when it arrives.
 */
class YapNPlayApp : Application() {
    override fun onCreate() {
        super.onCreate()
        if (GlobalContext.getOrNull() == null) {
            startKoin {
                androidContext(this@YapNPlayApp)
                modules(appModules + platformAppManagerModule)
            }
        }
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            get<AppManager>().syncWithRemote(fetchRemoteFlags())
        }
    }
}
