package com.kglabs28.yapnplay.appengine.features.notifications.domain

import kotlinx.datetime.Instant

/** One received push, kept in the bell's history (newest first). */
data class AppNotification(
    val id: String,
    val roomId: String,
    val title: String,
    val body: String,
    val receivedAt: Instant,
    val read: Boolean,
)
