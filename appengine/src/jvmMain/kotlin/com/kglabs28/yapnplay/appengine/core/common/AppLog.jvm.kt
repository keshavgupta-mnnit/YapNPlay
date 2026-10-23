package com.kglabs28.yapnplay.appengine.core.common

actual object AppLog {
    actual fun d(tag: String, message: String) {
        println("$tag: $message")
    }
}
