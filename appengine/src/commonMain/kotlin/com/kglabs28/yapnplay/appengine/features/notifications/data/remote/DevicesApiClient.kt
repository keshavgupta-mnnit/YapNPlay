package com.kglabs28.yapnplay.appengine.features.notifications.data.remote

import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.common.FakeDataProvider
import com.kglabs28.yapnplay.appengine.protocol.RegisterDevice
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.delay

/**
 * Push registration client. TODAY: pretends to register after the usual
 * fake delay. LATER: uncomment the production call, delete the fake line.
 */
class DevicesApiClient(
    private val client: HttpClient,
) : DevicesApi {

    override suspend fun registerDevice(body: RegisterDevice) {
        // --- production path (uncomment when backend is live) ---
        // client.post(AppConstants.SERVER_BASE_URL + "/devices") {
        //     contentType(ContentType.Application.Json)
        //     setBody(body)
        // }

        // --- fake path (delete when switching to production) ---
        delay(FakeDataProvider.NETWORK_DELAY_MS)
    }
}
