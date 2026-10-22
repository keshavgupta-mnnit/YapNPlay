package com.kglabs28.yapnplay.appengine.appmanager

import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.kglabs28.yapnplay.appengine.core.common.CrashReporter
import kotlinx.coroutines.tasks.await

/** [RemoteConfigSource] on Firebase Remote Config: every value as a string. */
class FirebaseRemoteConfigSource : RemoteConfigSource {
    override suspend fun fetch(defaults: Map<String, Any>): Map<String, String> = try {
        val remote = FirebaseRemoteConfig.getInstance()
        // Firebase's default re-fetch gap is 12 hours: new values (e.g. the ad unit) would wait that long.
        remote.setConfigSettingsAsync(FirebaseRemoteConfigSettings.Builder().setMinimumFetchIntervalInSeconds(AppConstants.REMOTE_CONFIG_FETCH_SECONDS).build()).await()
        remote.setDefaultsAsync(defaults).await()
        remote.fetchAndActivate().await()
        remote.all.mapValues { it.value.asString() }
    } catch (e: Exception) {
        CrashReporter.record(e, "FirebaseRemoteConfigSource")
        emptyMap()
    }
}
