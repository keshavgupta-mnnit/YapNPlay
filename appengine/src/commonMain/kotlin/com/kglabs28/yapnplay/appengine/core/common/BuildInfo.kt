package com.kglabs28.yapnplay.appengine.core.common

/**
 * What kind of build is running. The Android app sets [isDebug] from
 * BuildConfig.DEBUG at start; it stays false everywhere else.
 */
object BuildInfo {
    var isDebug: Boolean = false
}
