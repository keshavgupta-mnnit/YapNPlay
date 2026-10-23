package com.kglabs28.yapnplay.appengine.features.profile.usecase

import com.kglabs28.yapnplay.appengine.appmanager.helpers.AppManagerHelper
import com.kglabs28.yapnplay.appengine.core.common.DeepLinks
import com.kglabs28.yapnplay.appengine.core.ui.ProfileStrings

/**
 * What sharing the player's YapNPlay ID says: the app link that opens
 * YapNPlay and sends them a friend request, the store link for anyone
 * without the app, and the ID to type in. The app link can be switched
 * off (AppManager `invite_links_enabled`; always on in debug builds).
 */
class FriendInvites(private val appConfig: AppManagerHelper) {
    fun message(userCode: String): String =
        ProfileStrings.profileShareMessage(userCode, DeepLinks.friendLink(appConfig.serverBaseUrl(), userCode).takeIf { appConfig.areInviteLinksEnabled() }, appConfig.playStoreUrl())
}
