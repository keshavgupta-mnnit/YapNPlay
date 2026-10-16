package com.kglabs28.yapnplay.appengine.core.common

/**
 * Crash and non-fatal reporting. Every catch block in crash-sensitive
 * paths (network, storage, config) reports here as a non-fatal with
 * context — never let reporting itself throw, and never log secrets.
 */
expect object CrashReporter {
    fun record(exception: Throwable, context: String)
}
