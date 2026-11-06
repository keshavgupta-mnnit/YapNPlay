package com.kglabs28.yapnplay.appengine.core.common

/**
 * Desktop and web: the console (stdout / the browser console). No secrets in
 * the message — callers are responsible for that, this only prints what it receives.
 */
actual object CrashReporter {
    actual fun record(exception: Throwable, context: String) {
        try {
            println("CrashReporter[$context]: ${exception.message}")
        } catch (ignored: Exception) {
            // Reporting must never crash the app.
        }
    }
}
