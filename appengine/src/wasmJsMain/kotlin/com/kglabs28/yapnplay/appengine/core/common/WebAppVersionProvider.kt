package com.kglabs28.yapnplay.appengine.core.common

/** The web is always the latest build: a fixed label, never asked to update. */
class WebAppVersionProvider : AppVersionProvider {
    override fun versionName(): String = WEB_VERSION
    override fun versionCode(): Long = 0L

    private companion object {
        const val WEB_VERSION = "web"
    }
}
