package com.kglabs28.yapnplay.appengine.appmanager

import androidx.compose.runtime.Composable

/** Desktop and web builds never ask for an update (the web is always the latest). */
@Composable
actual fun rememberAppUpdater(): AppUpdater = object : AppUpdater {
    // Blank: never older than any rule.
    override val versionName: String = ""
    override suspend fun isUpdateAvailable(): Boolean = false
    override fun startUpdate() = Unit
}
