package com.kglabs28.yapnplay.appengine.core.common

import android.util.Log

actual object AppLog {
    actual var enabled: Boolean = false

    actual fun d(tag: String, message: String) {
        if (enabled) Log.d(tag, message)
    }
}
