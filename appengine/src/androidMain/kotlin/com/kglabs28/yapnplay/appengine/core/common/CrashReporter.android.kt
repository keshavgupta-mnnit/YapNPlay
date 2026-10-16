package com.kglabs28.yapnplay.appengine.core.common

import com.google.firebase.crashlytics.FirebaseCrashlytics

/**
 * Firebase Crashlytics backend. Fatal crashes report automatically;
 * caught exceptions arrive here as non-fatals with their context key.
 */
actual object CrashReporter {
    actual fun record(exception: Throwable, context: String) {
        try {
            FirebaseCrashlytics.getInstance().setCustomKey("context", context)
            FirebaseCrashlytics.getInstance().recordException(exception)
        } catch (ignored: Exception) {
            // Reporting must never crash the app.
        }
    }
}
