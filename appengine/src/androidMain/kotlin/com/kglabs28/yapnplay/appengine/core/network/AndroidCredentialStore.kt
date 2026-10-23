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
        // Written at once (commit, not apply): a restored account restarts the app right after,
        // and an asynchronous write would be lost with the process (the restore would loop).
        prefs.edit().putString(KEY_USER_ID, credentials.userId).putString(KEY_SECRET, credentials.secret).commit()
    }

    override fun loadPending(): BackendCredentials? {
        val userId = prefs.getString(KEY_PENDING_USER_ID, null) ?: return null
        val secret = prefs.getString(KEY_PENDING_SECRET, null) ?: return null
        return BackendCredentials(userId, secret)
    }

    override fun savePending(credentials: BackendCredentials?) {
        // Written at once: the point is to survive a crash right after.
        val edit = prefs.edit()
        if (credentials == null) edit.remove(KEY_PENDING_USER_ID).remove(KEY_PENDING_SECRET)
        else edit.putString(KEY_PENDING_USER_ID, credentials.userId).putString(KEY_PENDING_SECRET, credentials.secret)
        edit.commit()
    }

    override fun clear() {
        prefs.edit().clear().apply()
    }

    private companion object {
        const val PREFS_NAME = "yapnplay_account"
        const val KEY_USER_ID = "user_id"
        const val KEY_SECRET = "secret"
        const val KEY_PENDING_USER_ID = "pending_user_id"
        const val KEY_PENDING_SECRET = "pending_secret"
    }
}
