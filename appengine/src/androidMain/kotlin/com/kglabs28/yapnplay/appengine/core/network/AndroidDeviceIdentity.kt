package com.kglabs28.yapnplay.appengine.core.network

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings

/** Android's per-app id (ANDROID_ID): only this app sees it, and it stays until a factory reset. */
class AndroidDeviceIdentity(private val context: Context) : DeviceIdentity {
    // Used only to keep one guest account per phone, never for ads or tracking.
    @SuppressLint("HardwareIds")
    override fun deviceId(): String? =
        runCatching { Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID) }.getOrNull()?.takeIf { it.isNotBlank() }
}
