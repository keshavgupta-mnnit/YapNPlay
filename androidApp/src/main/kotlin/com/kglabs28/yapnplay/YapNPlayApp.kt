package com.kglabs28.yapnplay

import com.kglabs28.yapnplay.appengine.core.common.BuildInfo
import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.common.AppLog
import android.app.Application
import com.google.firebase.messaging.FirebaseMessaging
import com.kglabs28.yapnplay.appengine.core.common.CrashReporter
import com.kglabs28.yapnplay.appengine.appmanager.AppManager
import com.kglabs28.yapnplay.appengine.appmanager.RemoteConfigSource
import com.kglabs28.yapnplay.appengine.appmanager.AppManagerKeys
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
        // Debug logs (ads, screen actions) in debug builds only; ads are always on there too.
        BuildInfo.isDebug = BuildConfig.DEBUG
        AppLog.enabled = BuildConfig.DEBUG
        AppLog.d(AppConstants.LOG_TAG_AUTH, "app started (debug logs on)")
        if (GlobalContext.getOrNull() == null) {
            startKoin {
                androidContext(this@YapNPlayApp)
                modules(appModules + platformAppManagerModule)
            }
        }
        RoomNotifications.createChannel(this)
        registerForPush()
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            get<AppManager>().syncWithRemote(get<RemoteConfigSource>().fetch(AppManagerKeys.defaults))
        }
    }

    /** Sends the current FCM token to the server (new tokens arrive in the service). */
    private fun registerForPush() {
        try {
            FirebaseMessaging.getInstance().token
                .addOnSuccessListener { YapNPlayMessagingService.registerPushToken(it) }
                .addOnFailureListener { CrashReporter.record(it, "YapNPlayApp.fcmToken") }
        } catch (e: Exception) {
            CrashReporter.record(e, "YapNPlayApp.registerForPush")
        }
    }
}
