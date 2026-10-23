package com.kglabs28.yapnplay.appengine.core.network

/**
 * Which phone this is, sent with guest sign-up so the same phone gets its
 * guest back (after a reinstall) instead of a new account. Android: its
 * per-app id (the same for this app until a factory reset).
 */
interface DeviceIdentity {
    /** Null where the platform has none (desktop, previews). */
    fun deviceId(): String?
}

/** Desktop / previews: no device id. */
class NoDeviceIdentity : DeviceIdentity {
    override fun deviceId(): String? = null
}
