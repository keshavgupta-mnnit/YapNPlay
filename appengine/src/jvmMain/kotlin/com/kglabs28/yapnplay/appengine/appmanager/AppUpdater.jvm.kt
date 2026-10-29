package com.kglabs28.yapnplay.appengine.appmanager

import androidx.compose.runtime.Composable

/** Desktop preview builds never ask for an update. */
@Composable
actual fun rememberAppUpdater(): AppUpdater = object : AppUpdater {
    // Blank: never older than any rule.
    override val versionName: String = ""
    override suspend fun isUpdateAvailable(): Boolean = false
    override fun startUpdate() = Unit
}
