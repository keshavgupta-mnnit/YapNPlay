package com.kglabs28.yapnplay.appengine.core.common

import android.os.Build
import android.content.Context

/** Reads `versionName` and `versionCode` of the installed app from the package manager. */
class AndroidAppVersionProvider(private val context: Context) : AppVersionProvider {
    @Suppress("DEPRECATION") // the flags-as-Int overload is fine down to minSdk 26
    override fun versionName(): String =
        context.packageManager.getPackageInfo(context.packageName, 0).versionName.orEmpty()

    @Suppress("DEPRECATION") // versionCode is the only one below Android 9
    override fun versionCode(): Long {
        val info = context.packageManager.getPackageInfo(context.packageName, 0)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) info.longVersionCode else info.versionCode.toLong()
    }
}
