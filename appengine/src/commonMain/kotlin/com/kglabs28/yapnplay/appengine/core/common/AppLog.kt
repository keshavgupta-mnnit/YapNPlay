package com.kglabs28.yapnplay.appengine.core.common

/**
 * Debug log lines (Logcat on Android, stdout on the desktop), for tracing
 * a flow on a device. [tag] groups a flow's lines, e.g. "YapAds". Nothing
 * is written unless [enabled]: the Android app turns it on for debug
 * builds only (BuildConfig.DEBUG), so release builds log nothing.
 */
expect object AppLog {
    var enabled: Boolean
    fun d(tag: String, message: String)
}
