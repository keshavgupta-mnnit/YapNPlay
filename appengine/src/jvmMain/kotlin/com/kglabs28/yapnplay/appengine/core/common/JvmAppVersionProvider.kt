package com.kglabs28.yapnplay.appengine.core.common

/** Desktop builds (tests only) have no installed package: a fixed label. */
class JvmAppVersionProvider : AppVersionProvider {
    override fun versionName(): String = DEV_VERSION

    override fun versionCode(): Long = 0L

    private companion object {
        const val DEV_VERSION = "dev"
    }
}
