package com.kglabs28.yapnplay.appengine.features.notifications.data.remote

import com.kglabs28.yapnplay.appengine.protocol.RegisterDevice

/** Device registration for push. Shapes match docs/backend-api-contract.md. */
interface DevicesApi {
    /** `POST /devices` — idempotent; the latest token for this user+device wins. */
    suspend fun registerDevice(body: RegisterDevice)
}
