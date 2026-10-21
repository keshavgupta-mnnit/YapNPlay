package com.kglabs28.yapnplay.appengine.appmanager.helpers

import com.kglabs28.yapnplay.appengine.appmanager.AppManager
import com.kglabs28.yapnplay.appengine.appmanager.AppManagerKeys
import com.kglabs28.yapnplay.appengine.appmanager.ConfigKey

/** Game kill switches and table-wide timings. */
class GamesAppManagerHelper(private val appManager: AppManager) {

    /** Whether a registered game may be shown and queued (game_enabled_<id>). */
    fun isGameEnabled(gameId: String): Boolean = appManager.get(ConfigKey(AppManagerKeys.Games.enabledKey(gameId), true, ""))

    fun turnHurrySeconds(): Long = appManager.get(AppManagerKeys.Games.TURN_HURRY_SECONDS)
}
