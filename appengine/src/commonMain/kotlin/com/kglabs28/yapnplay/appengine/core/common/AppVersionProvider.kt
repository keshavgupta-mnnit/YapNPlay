package com.kglabs28.yapnplay.appengine.core.common

/**
 * The installed app's version name (e.g. "1.0.0") and version code (e.g.
 * 29), read from the platform, so the build file is the only place they're set.
 */
interface AppVersionProvider {
    fun versionName(): String
    fun versionCode(): Long
}
