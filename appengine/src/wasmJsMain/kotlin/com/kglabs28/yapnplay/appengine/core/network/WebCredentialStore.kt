package com.kglabs28.yapnplay.appengine.core.network

import com.kglabs28.yapnplay.appengine.core.common.BrowserStorage

/**
 * The signed-in account in the browser's storage, so a returning visit
 * goes straight to the app. The web never makes a guest, so nothing is
 * ever pending.
 */
class WebCredentialStore : CredentialStore {
    private val storage = BrowserStorage.local

    override fun load(): BackendCredentials? {
        val userId = storage.get(USER_ID) ?: return null
        val secret = storage.get(SECRET) ?: return null
        return BackendCredentials(userId, secret)
    }

    override fun save(credentials: BackendCredentials) {
        storage.set(USER_ID, credentials.userId)
        storage.set(SECRET, credentials.secret)
    }

    override fun clear() {
        storage.set(USER_ID, null)
        storage.set(SECRET, null)
    }

    override fun loadPending(): BackendCredentials? = null
    override fun savePending(credentials: BackendCredentials?) = Unit

    override fun welcomeNeeded(): Boolean = storage.get(WELCOME) == TRUE
    override fun setWelcomeNeeded(needed: Boolean) = storage.set(WELCOME, if (needed) TRUE else null)

    private companion object {
        const val USER_ID = "yapnplay.userId"
        const val SECRET = "yapnplay.secret"
        const val WELCOME = "yapnplay.welcome"
        const val TRUE = "1"
    }
}
