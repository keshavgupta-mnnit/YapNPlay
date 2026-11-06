package com.kglabs28.yapnplay.appengine.core.common

import kotlinx.browser.localStorage
import kotlinx.browser.sessionStorage
import org.w3c.dom.Storage

/**
 * The browser's key-value storage. [local] lasts across visits (the
 * account, settings); [session] only for this tab (the sign-in nonce).
 * Private windows or blocked site data can make it throw: then nothing is
 * kept and the app still works (the login screen shows again next time).
 */
class BrowserStorage private constructor(private val storage: () -> Storage) {
    fun get(key: String): String? = try {
        storage().getItem(key)
    } catch (e: Throwable) {
        null
    }

    fun set(key: String, value: String?) {
        try {
            if (value == null) storage().removeItem(key) else storage().setItem(key, value)
        } catch (e: Throwable) {
            // Storage blocked: kept for this page load only.
        }
    }

    companion object {
        val local = BrowserStorage { localStorage }
        val session = BrowserStorage { sessionStorage }
    }
}
