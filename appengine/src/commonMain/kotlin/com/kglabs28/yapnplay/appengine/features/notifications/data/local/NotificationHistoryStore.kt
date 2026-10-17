package com.kglabs28.yapnplay.appengine.features.notifications.data.local

/**
 * Where the notification history is persisted, as one JSON string.
 * Android keeps it in SharedPreferences so pushes received while the app
 * was closed are still listed on the next launch.
 */
interface NotificationHistoryStore {
    fun load(): String?
    fun save(json: String)
}

/** Desktop / previews: history lives only while the process runs. */
class InMemoryNotificationHistoryStore : NotificationHistoryStore {
    private var json: String? = null
    override fun load(): String? = json
    override fun save(json: String) {
        this.json = json
    }
}
