package com.kglabs28.yapnplay.appengine.features.notifications.repository

import com.kglabs28.yapnplay.appengine.core.common.AppResult

/** Tells the server where to send this user's push notifications. */
interface DevicesRepository {
    suspend fun registerPushToken(token: String, platform: String): AppResult<Unit>
}
