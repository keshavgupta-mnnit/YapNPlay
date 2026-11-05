package com.kglabs28.yapnplay.appengine.ui.components.common

import androidx.compose.runtime.Composable

/** Runtime permissions the app asks for. */
enum class AppPermission(
    /** Asked for on the first launch (Nearby only when the user hosts or scans). */
    val askedAtStartup: Boolean = true,
) {
    MICROPHONE,
    NOTIFICATIONS,
    /** Finding and linking phones around (Bluetooth, nearby Wi-Fi) for offline play. */
    NEARBY(askedAtStartup = false),
}

/**
 * Check / ask / send-to-Settings for one permission, from a composable.
 * [request] shows the system prompt (or reports immediately when already
 * granted) and calls back with the answer.
 */
class PermissionRequester(
    val isGranted: () -> Boolean,
    val request: (onResult: (Boolean) -> Unit) -> Unit,
    val openSettings: () -> Unit,
)

@Composable
expect fun rememberPermissionRequester(permission: AppPermission): PermissionRequester

/**
 * Asks, once per app start, for every permission not granted yet
 * (microphone; notifications on Android 13+). Denying is fine: the mic is
 * asked for again when the user turns it on. Waits for [ready] (the launch's
 * Play Games sign-in done), so the request never closes that dialog.
 */
@Composable
expect fun RequestStartupPermissions(ready: Boolean)
