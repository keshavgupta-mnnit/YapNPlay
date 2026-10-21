package com.kglabs28.yapnplay.appengine.core.common

/** Desktop builds (tests only) have no installed package: a fixed label. */
class JvmAppVersionProvider : AppVersionProvider {
    override fun versionName(): String = DEV_VERSION

    private companion object {
        const val DEV_VERSION = "dev"
    }
}
