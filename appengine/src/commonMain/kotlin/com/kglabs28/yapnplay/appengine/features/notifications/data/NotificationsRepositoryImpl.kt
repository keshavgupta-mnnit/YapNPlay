package com.kglabs28.yapnplay.appengine.features.notifications.data

import com.kglabs28.yapnplay.appengine.features.notifications.data.local.NotificationsLocalManager
import com.kglabs28.yapnplay.appengine.features.notifications.domain.AppNotification
import com.kglabs28.yapnplay.appengine.features.notifications.repository.NotificationsRepository
import kotlinx.coroutines.flow.StateFlow

/** Local only: pushes arrive on the device, so the history lives here. */
class NotificationsRepositoryImpl(
    private val local: NotificationsLocalManager,
) : NotificationsRepository {

    override val history: StateFlow<List<AppNotification>> get() = local.history

    override fun add(notification: AppNotification) = local.add(notification)

    override fun markAllRead() = local.markAllRead()
}
