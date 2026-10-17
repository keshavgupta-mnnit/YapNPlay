package com.kglabs28.yapnplay.appengine.features.notifications.usecase

import com.kglabs28.yapnplay.appengine.core.common.AppClock
import com.kglabs28.yapnplay.appengine.features.notifications.domain.AppNotification
import com.kglabs28.yapnplay.appengine.features.notifications.repository.NotificationsRepository
import com.kglabs28.yapnplay.appengine.protocol.PushKeys

/**
 * Adds a received push (its FCM data map) to the history. Only room
 * activity is kept; anything else is ignored.
 */
class SaveNotificationUseCase(
    private val repository: NotificationsRepository,
    private val clock: AppClock,
) {
    operator fun invoke(data: Map<String, String>) {
        if (data[PushKeys.TYPE] != PushKeys.TYPE_ROOM_ACTIVITY) return
        val roomId = data[PushKeys.ROOM_ID] ?: return
        val now = clock.now()
        repository.add(
            AppNotification(
                id = "$roomId-${now.toEpochMilliseconds()}",
                roomId = roomId,
                title = data[PushKeys.TITLE] ?: data[PushKeys.ROOM_NAME].orEmpty(),
                body = data[PushKeys.BODY].orEmpty(),
                receivedAt = now,
                read = false,
            ),
        )
    }
}
