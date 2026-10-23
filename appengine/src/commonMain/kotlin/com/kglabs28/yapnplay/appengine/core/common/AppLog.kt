package com.kglabs28.yapnplay.appengine.core.common

/**
 * Debug log lines (Logcat on Android, stdout on the desktop), for tracing
 * a flow on a device. [tag] groups a flow's lines, e.g. "YapAds".
 */
expect object AppLog {
    fun d(tag: String, message: String)
}
