package com.kglabs28.yapnplay.appengine.core.common

import android.util.Log

actual object AppLog {
    actual fun d(tag: String, message: String) {
        Log.d(tag, message)
    }
}
