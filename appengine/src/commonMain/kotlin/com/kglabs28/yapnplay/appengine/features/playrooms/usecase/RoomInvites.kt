package com.kglabs28.yapnplay.appengine.features.playrooms.usecase

import com.kglabs28.yapnplay.appengine.core.common.DeepLinks
import com.kglabs28.yapnplay.appengine.appmanager.helpers.AppManagerHelper
import com.kglabs28.yapnplay.appengine.core.ui.RoomStrings

/**
 * What an invite to a room says: the app link that opens YapNPlay on
 * "Join this room?", the store link for anyone without the app, and the
 * room code to type in. The app link can be switched off (AppManager
 * `invite_links_enabled`; always on in debug builds).
 */
class RoomInvites(private val appConfig: AppManagerHelper) {
    /** The tap-to-join link. */
    fun link(code: String): String = DeepLinks.joinLink(appConfig.serverBaseUrl(), code)

    fun message(roomName: String, code: String): String =
        RoomStrings.roomInviteMessage(roomName, code, link(code).takeIf { appConfig.areInviteLinksEnabled() }, appConfig.playStoreUrl())
}
