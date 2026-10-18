package com.kglabs28.yapnplay.appengine.features.notifications.data.remote

import com.kglabs28.yapnplay.appengine.core.network.BackendPaths
import com.kglabs28.yapnplay.appengine.protocol.RegisterDevice
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

/** `POST /devices`: tells the backend this phone's Firebase push token. */
class DevicesApiClient(
    private val client: HttpClient,
) : DevicesApi {

    override suspend fun registerDevice(body: RegisterDevice) {
        client.post(BackendPaths.DEVICES) {
            contentType(ContentType.Application.Json)
            setBody(body)
        }
    }
}
