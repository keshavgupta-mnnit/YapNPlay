package com.kglabs28.yapnplay.appengine.core.common

actual object AppLog {
    actual var enabled: Boolean = true

    actual fun d(tag: String, message: String) {
        if (enabled) println("$tag: $message")
    }
}
