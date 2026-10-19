package com.kglabs28.yapnplay.appengine.core.common

/**
 * The installed app's version name (e.g. "1.0.0"), read from the platform,
 * so the build file's `versionName` is the only place it's set.
 */
interface AppVersionProvider {
    fun versionName(): String
}
