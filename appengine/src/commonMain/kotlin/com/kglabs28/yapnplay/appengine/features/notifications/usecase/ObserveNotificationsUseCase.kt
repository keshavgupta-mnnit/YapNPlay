package com.kglabs28.yapnplay.appengine.features.notifications.usecase

import com.kglabs28.yapnplay.appengine.features.notifications.domain.AppNotification
import com.kglabs28.yapnplay.appengine.features.notifications.repository.NotificationsRepository
import kotlinx.coroutines.flow.StateFlow

/** The bell's list, newest first (at most 10). */
class ObserveNotificationsUseCase(private val repository: NotificationsRepository) {
    operator fun invoke(): StateFlow<List<AppNotification>> = repository.history
}
