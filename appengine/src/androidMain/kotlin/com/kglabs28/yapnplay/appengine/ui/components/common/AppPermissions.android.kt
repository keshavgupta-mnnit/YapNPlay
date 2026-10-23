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

/** Android permission name, or null when the OS version needs no prompt. */
private fun AppPermission.manifestName(): String? = when (this) {
    AppPermission.MICROPHONE -> Manifest.permission.RECORD_AUDIO
    AppPermission.NOTIFICATIONS ->
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.POST_NOTIFICATIONS else null
}

private fun Context.isGranted(permission: AppPermission): Boolean {
    val name = permission.manifestName() ?: return true
    return ContextCompat.checkSelfPermission(this, name) == PackageManager.PERMISSION_GRANTED
}

@Composable
actual fun rememberPermissionRequester(permission: AppPermission): PermissionRequester {
    val context = LocalContext.current
    val pending = remember { arrayOfNulls<(Boolean) -> Unit>(1) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        pending[0]?.invoke(granted)
        pending[0] = null
    }
    return remember(context, launcher) {
        PermissionRequester(
            isGranted = { context.isGranted(permission) },
            request = { onResult ->
                val name = permission.manifestName()
                if (name == null || context.isGranted(permission)) {
                    onResult(true)
                } else {
                    pending[0] = onResult
                    launcher.launch(name)
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
 * it again. The mic is asked for again only when the user turns it on.
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
            .filterNot { context.isGranted(it) }
            .mapNotNull { it.manifestName() }
        if (missing.isNotEmpty()) launcher.launch(missing.toTypedArray())
    }
}

private const val PREFS_NAME = "yapnplay_permissions"
private const val KEY_STARTUP_ASKED = "startup_permissions_asked"
