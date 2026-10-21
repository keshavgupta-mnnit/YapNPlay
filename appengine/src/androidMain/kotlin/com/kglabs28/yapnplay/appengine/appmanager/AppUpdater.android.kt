package com.kglabs28.yapnplay.appengine.appmanager

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.kglabs28.yapnplay.appengine.core.common.CrashReporter

@Composable
actual fun rememberAppUpdater(): AppUpdater {
    val context = LocalContext.current
    return remember(context) { PlayAppUpdater(context) }
}

/**
 * Google Play In-App Updates: the full-screen "update now" flow when Play
 * has a newer build for this install, otherwise the app's store page
 * (also for installs that did not come from Play, e.g. a debug APK).
 */
private class PlayAppUpdater(private val context: Context) : AppUpdater {

    override val versionName: String = runCatching {
        context.packageManager.getPackageInfo(context.packageName, 0).versionName
    }.getOrNull().orEmpty()

    override fun startUpdate() {
        val activity = context.findActivity() ?: return openStore()
        val manager = AppUpdateManagerFactory.create(context)
        manager.appUpdateInfo
            .addOnSuccessListener { info ->
                val inPlace = info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                    info.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
                if (inPlace) {
                    manager.startUpdateFlow(info, activity, AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build())
                } else {
                    openStore()
                }
            }
            .addOnFailureListener {
                CrashReporter.record(it, "AppUpdater.startUpdate")
                openStore()
            }
    }

    private fun openStore() {
        val market = Intent(Intent.ACTION_VIEW, Uri.parse(MARKET_URL + context.packageName)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            context.startActivity(market)
        } catch (e: ActivityNotFoundException) {
            val web = Intent(Intent.ACTION_VIEW, Uri.parse(WEB_URL + context.packageName)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            runCatching { context.startActivity(web) }.onFailure { CrashReporter.record(it, "AppUpdater.openStore") }
        }
    }

    private tailrec fun Context.findActivity(): Activity? = when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }

    private companion object {
        const val MARKET_URL = "market://details?id="
        const val WEB_URL = "https://play.google.com/store/apps/details?id="
    }
}
