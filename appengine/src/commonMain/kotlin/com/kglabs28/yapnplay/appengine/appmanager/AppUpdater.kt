package com.kglabs28.yapnplay.appengine.appmanager

import androidx.compose.runtime.Composable

/** This build's version (`1.0.0` style) and the platform's way to install a newer one. */
interface AppUpdater {
    val versionName: String

    /** Opens Google Play's update screen, or the store page when Play cannot update in place. */
    fun startUpdate()
}

@Composable
expect fun rememberAppUpdater(): AppUpdater
