package com.kglabs28.yapnplay.appengine.core.network

import android.content.Context

/** The guest account in the app's private SharedPreferences. */
class AndroidCredentialStore(context: Context) : CredentialStore {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun load(): BackendCredentials? {
        val userId = prefs.getString(KEY_USER_ID, null) ?: return null
        val secret = prefs.getString(KEY_SECRET, null) ?: return null
        return BackendCredentials(userId, secret)
    }

    override fun save(credentials: BackendCredentials) {
        prefs.edit().putString(KEY_USER_ID, credentials.userId).putString(KEY_SECRET, credentials.secret).apply()
    }

    override fun clear() {
        prefs.edit().clear().apply()
    }

    private companion object {
        const val PREFS_NAME = "yapnplay_account"
        const val KEY_USER_ID = "user_id"
        const val KEY_SECRET = "secret"
    }
}
