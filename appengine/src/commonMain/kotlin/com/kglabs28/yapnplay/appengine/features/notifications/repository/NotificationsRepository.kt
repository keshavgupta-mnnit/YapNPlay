package com.kglabs28.yapnplay.appengine.features.notifications.repository

import com.kglabs28.yapnplay.appengine.features.notifications.domain.AppNotification
import kotlinx.coroutines.flow.StateFlow

/** Received pushes, kept on the device for the top bar bell. */
interface NotificationsRepository {
    val history: StateFlow<List<AppNotification>>
    fun add(notification: AppNotification)
    fun markAllRead()
}
