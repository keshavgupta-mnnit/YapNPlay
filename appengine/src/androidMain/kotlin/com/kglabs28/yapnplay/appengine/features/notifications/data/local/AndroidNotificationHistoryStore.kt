package com.kglabs28.yapnplay.appengine.features.notifications.data.local

import android.content.Context

/** SharedPreferences-backed history, so it survives app restarts. */
class AndroidNotificationHistoryStore(context: Context) : NotificationHistoryStore {

    private val prefs = context.getSharedPreferences("yapnplay_notifications", Context.MODE_PRIVATE)

    override fun load(): String? = prefs.getString(KEY_HISTORY, null)

    override fun save(json: String) {
        prefs.edit().putString(KEY_HISTORY, json).apply()
    }

    private companion object {
        const val KEY_HISTORY = "history"
    }
}
