package com.kglabs28.yapnplay.appengine.features.notifications.data

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.core.common.CrashReporter
import com.kglabs28.yapnplay.appengine.core.common.DispatcherProvider
import com.kglabs28.yapnplay.appengine.core.common.YapNPlayError
import com.kglabs28.yapnplay.appengine.features.notifications.data.remote.DevicesApi
import com.kglabs28.yapnplay.appengine.features.notifications.repository.DevicesRepository
import com.kglabs28.yapnplay.appengine.protocol.RegisterDevice
import kotlinx.coroutines.withContext

/** Registers the push token. Never throws; failures are reported and typed. */
class DevicesRepositoryImpl(
    private val api: DevicesApi,
    private val dispatchers: DispatcherProvider,
) : DevicesRepository {

    override suspend fun registerPushToken(token: String, platform: String): AppResult<Unit> =
        withContext(dispatchers.io) {
            try {
                api.registerDevice(RegisterDevice(token = token, platform = platform))
                AppResult.Success(Unit)
            } catch (e: Exception) {
                CrashReporter.record(e, "DevicesRepository.registerPushToken")
                AppResult.Failure(YapNPlayError.Network("device registration failed"))
            }
        }
}
