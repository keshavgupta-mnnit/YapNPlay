package com.kglabs28.yapnplay.appengine.core.common

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics

/**
 * Firebase Crashlytics backend. Fatal crashes report automatically;
 * caught exceptions arrive here as non-fatals with their context key.
 */
actual object CrashReporter {
    private const val LOG_TAG = "YapNPlay"

    actual fun record(exception: Throwable, context: String) {
        // Also in logcat (`adb logcat -s YapNPlay`), so failures show while testing.
        Log.w(LOG_TAG, context, exception)
        try {
            FirebaseCrashlytics.getInstance().setCustomKey("context", context)
            FirebaseCrashlytics.getInstance().recordException(exception)
        } catch (ignored: Exception) {
            // Reporting must never crash the app.
        }
    }
}
