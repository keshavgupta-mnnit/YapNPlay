package com.kglabs28.yapnplay.appengine.core.common

import android.content.Context

/** Reads `versionName` of the installed app from the package manager. */
class AndroidAppVersionProvider(private val context: Context) : AppVersionProvider {
    @Suppress("DEPRECATION") // the flags-as-Int overload is fine down to minSdk 26
    override fun versionName(): String =
        context.packageManager.getPackageInfo(context.packageName, 0).versionName.orEmpty()
}
