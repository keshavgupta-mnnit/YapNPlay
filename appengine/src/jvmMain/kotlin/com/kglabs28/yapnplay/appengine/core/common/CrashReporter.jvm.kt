package com.kglabs28.yapnplay.appengine.core.common

/**
 * JVM backend (unit tests, desktop): stderr. No secrets in the message —
 * callers are responsible for that, this only prints what it receives.
 */
actual object CrashReporter {
    actual fun record(exception: Throwable, context: String) {
        try {
            System.err.println("CrashReporter[$context]: ${exception.message}")
        } catch (ignored: Exception) {
            // Reporting must never crash the app.
        }
    }
}
