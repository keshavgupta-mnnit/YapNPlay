package com.kglabs28.yapnplay.appengine.features.about.data

import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.features.about.domain.AppInfo
import com.kglabs28.yapnplay.appengine.protocol.transport.CONTRACT_VERSION

/**
 * Static app metadata source. Later phases add remote config and storage
 * behind this same seam; callers never change.
 */
class StaticAppInfoSource {
    fun load(): AppInfo = AppInfo(
        appName = "YapNPlay",
        version = AppConstants.APP_VERSION,
        contractVersion = CONTRACT_VERSION,
    )
}
