package com.kglabs28.yapnplay.appengine.ui.components.common

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

/** The Android permissions behind [AppPermission] on this OS version (empty: no prompt needed). */
private fun AppPermission.manifestNames(): List<String> = when (this) {
    AppPermission.MICROPHONE -> listOf(Manifest.permission.RECORD_AUDIO)
    AppPermission.NOTIFICATIONS ->
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) listOf(Manifest.permission.POST_NOTIFICATIONS) else emptyList()
    // Nearby Connections: Bluetooth (12+) and nearby Wi-Fi (13+), and location on every version
    // (many phones still refuse to find others without it).
    AppPermission.NEARBY -> when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> listOf(
            Manifest.permission.BLUETOOTH_ADVERTISE,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.NEARBY_WIFI_DEVICES,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
        )
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> listOf(
            Manifest.permission.BLUETOOTH_ADVERTISE,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
        )
        else -> listOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
    }
}

private fun Context.isGranted(permission: AppPermission): Boolean =
    permission.manifestNames().all { ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED }

@Composable
actual fun rememberPermissionRequester(permission: AppPermission): PermissionRequester {
    val context = LocalContext.current
    val pending = remember { arrayOfNulls<(Boolean) -> Unit>(1) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
        pending[0]?.invoke(results.values.all { it } && context.isGranted(permission))
        pending[0] = null
    }
    return remember(context, launcher) {
        PermissionRequester(
            isGranted = { context.isGranted(permission) },
            request = { onResult ->
                val names = permission.manifestNames()
                if (names.isEmpty() || context.isGranted(permission)) {
                    onResult(true)
                } else {
                    pending[0] = onResult
                    launcher.launch(names.toTypedArray())
                }
            },
            openSettings = {
                context.startActivity(
                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", context.packageName, null))
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                )
            },
        )
    }
}

/**
 * First launch only: asks for every permission not yet granted, then
 * remembers (on this device) that it asked, so later launches never show
 * it again. The mic is asked for again only when the user turns it on;
 * Nearby only when the user hosts or scans.
 */
@Composable
actual fun RequestStartupPermissions(ready: Boolean) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { }
    LaunchedEffect(ready) {
        if (!ready) return@LaunchedEffect
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        if (prefs.getBoolean(KEY_STARTUP_ASKED, false)) return@LaunchedEffect
        prefs.edit().putBoolean(KEY_STARTUP_ASKED, true).apply()
        val missing = AppPermission.entries
            .filter { it.askedAtStartup }
            .filterNot { context.isGranted(it) }
            .flatMap { it.manifestNames() }
        if (missing.isNotEmpty()) launcher.launch(missing.toTypedArray())
    }
}

private const val PREFS_NAME = "yapnplay_permissions"
private const val KEY_STARTUP_ASKED = "startup_permissions_asked"
