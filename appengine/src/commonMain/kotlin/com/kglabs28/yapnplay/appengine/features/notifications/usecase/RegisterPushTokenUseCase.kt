package com.kglabs28.yapnplay.appengine.features.notifications.usecase

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.features.notifications.repository.DevicesRepository

/**
 * Sends this device's push token to the server (on first launch and
 * whenever the token changes), so room-activity pushes can reach it.
 */
class RegisterPushTokenUseCase(private val repository: DevicesRepository) {

    suspend operator fun invoke(token: String, platform: String): AppResult<Unit> =
        if (token.isBlank()) AppResult.Success(Unit) else repository.registerPushToken(token, platform)
}
