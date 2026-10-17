package com.kglabs28.yapnplay.appengine.features.notifications.data.local

import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.common.CrashReporter
import com.kglabs28.yapnplay.appengine.features.notifications.domain.AppNotification
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

/**
 * The bell's history: newest first, capped at
 * [AppConstants.NOTIFICATION_HISTORY_MAX]. Read once from [store], then
 * every change is written back. Safe to call from the push service
 * thread and the UI at the same time.
 */
class NotificationsLocalManager(private val store: NotificationHistoryStore) {

    private val json = Json { ignoreUnknownKeys = true }
    private val _history = MutableStateFlow(read())
    val history: StateFlow<List<AppNotification>> = _history.asStateFlow()

    fun add(notification: AppNotification) = change { list ->
        (listOf(notification) + list.filterNot { it.id == notification.id })
            .take(AppConstants.NOTIFICATION_HISTORY_MAX)
    }

    fun markAllRead() = change { list ->
        if (list.none { !it.read }) list else list.map { it.copy(read = true) }
    }

    private fun change(transform: (List<AppNotification>) -> List<AppNotification>) {
        _history.update(transform)
        write(_history.value)
    }

    private fun read(): List<AppNotification> = try {
        store.load()?.let { text ->
            json.decodeFromString(ListSerializer(StoredNotification.serializer()), text).map { it.toDomain() }
        }.orEmpty()
    } catch (e: Exception) {
        CrashReporter.record(e, "NotificationsLocalManager.read")
        emptyList()
    }

    private fun write(list: List<AppNotification>) {
        try {
            store.save(json.encodeToString(ListSerializer(StoredNotification.serializer()), list.map { it.toStored() }))
        } catch (e: Exception) {
            CrashReporter.record(e, "NotificationsLocalManager.write")
        }
    }

    @Serializable
    private data class StoredNotification(
        val id: String,
        val roomId: String,
        val title: String,
        val body: String,
        val receivedAtMs: Long,
        val read: Boolean,
    ) {
        fun toDomain() = AppNotification(id, roomId, title, body, Instant.fromEpochMilliseconds(receivedAtMs), read)
    }

    private fun AppNotification.toStored() =
        StoredNotification(id, roomId, title, body, receivedAt.toEpochMilliseconds(), read)
}
