package com.kglabs28.yapnplay.appengine.appmanager

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.kglabs28.yapnplay.appengine.core.common.CrashReporter
import kotlinx.coroutines.tasks.await

/**
 * Fetches Remote Config and returns every value as strings.
 * Never throws: failures report to Crashlytics and yield an empty map,
 * which keeps local values in force.
 */
suspend fun fetchRemoteFlags(): Map<String, String> {
    return try {
        val remote = FirebaseRemoteConfig.getInstance()
        remote.setDefaultsAsync(
            mapOf(
                AppManagerConstants.Key.VOICE_ENABLED to AppManagerConstants.Default.VOICE_ENABLED,
                AppManagerConstants.Key.ROYAL_LUDO_ENABLED to AppManagerConstants.Default.ROYAL_LUDO_ENABLED,
                AppManagerConstants.Key.PARTY_CHAT_ENABLED to AppManagerConstants.Default.PARTY_CHAT_ENABLED,
                AppManagerConstants.Key.UPDATE_CONFIG to AppManagerConstants.Default.UPDATE_CONFIG,
            ),
        ).await()
        remote.fetchAndActivate().await()
        remote.all.mapValues { it.value.asString() }
    } catch (e: Exception) {
        CrashReporter.record(e, "fetchRemoteFlags")
        emptyMap()
    }
}
