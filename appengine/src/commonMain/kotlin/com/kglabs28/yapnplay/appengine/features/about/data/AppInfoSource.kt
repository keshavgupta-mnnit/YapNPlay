package com.kglabs28.yapnplay.appengine.features.about.data

import com.kglabs28.yapnplay.appengine.core.common.AppVersionProvider
import com.kglabs28.yapnplay.appengine.features.about.domain.AppInfo
import com.kglabs28.yapnplay.appengine.protocol.transport.CONTRACT_VERSION

/**
 * App metadata: the installed version (from the platform) and the contract
 * version. Later phases add remote config and storage
 * behind this same seam; callers never change.
 */
class StaticAppInfoSource(private val versions: AppVersionProvider) {
    fun load(): AppInfo = AppInfo(
        appName = "YapNPlay",
        version = versions.versionName(),
        contractVersion = CONTRACT_VERSION,
    )
}
