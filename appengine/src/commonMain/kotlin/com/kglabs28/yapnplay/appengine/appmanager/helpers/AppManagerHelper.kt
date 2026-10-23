package com.kglabs28.yapnplay.appengine.appmanager.helpers

import com.kglabs28.yapnplay.appengine.core.common.BuildInfo
import com.kglabs28.yapnplay.appengine.appmanager.AppManager
import com.kglabs28.yapnplay.appengine.appmanager.AppManagerKeys
import com.kglabs28.yapnplay.appengine.core.common.AppConstants

/** App-wide remote values: backend address, update rule, voice / party chat switches, room limit, share link. */
class AppManagerHelper(private val appManager: AppManager) {

    /** The backend; anything that isn't an http(s) address falls back to the built-in one. */
    fun serverBaseUrl(): String =
        appManager.get(AppManagerKeys.App.SERVER_BASE_URL).trim().trimEnd('/')
            .takeIf { it.startsWith(HTTPS) || it.startsWith(HTTP) }
            ?: AppConstants.SERVER_BASE_URL

    fun updateConfig(): String = appManager.get(AppManagerKeys.App.UPDATE_CONFIG)
    fun isVoiceEnabled(): Boolean = appManager.get(AppManagerKeys.App.VOICE_ENABLED)
    fun isPartyChatEnabled(): Boolean = appManager.get(AppManagerKeys.App.PARTY_CHAT_ENABLED)
    fun maxRoomsPerUser(): Int = appManager.get(AppManagerKeys.App.MAX_ROOMS_PER_USER)
    fun playStoreUrl(): String = appManager.get(AppManagerKeys.App.PLAY_STORE_URL)
    /** Invites carry the tap-to-open app link: always in debug builds, else as `invite_links_enabled` says (on by default). */
    fun areInviteLinksEnabled(): Boolean = BuildInfo.isDebug || appManager.get(AppManagerKeys.App.INVITE_LINKS_ENABLED)

    private companion object {
        const val HTTPS = "https://"
        const val HTTP = "http://"
    }
}
