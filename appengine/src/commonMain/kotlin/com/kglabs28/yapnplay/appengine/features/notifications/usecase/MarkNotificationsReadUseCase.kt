package com.kglabs28.yapnplay.appengine.features.notifications.usecase

import com.kglabs28.yapnplay.appengine.features.notifications.repository.NotificationsRepository

/** Clears the unread badge once the user has seen the list. */
class MarkNotificationsReadUseCase(private val repository: NotificationsRepository) {
    operator fun invoke() = repository.markAllRead()
}
