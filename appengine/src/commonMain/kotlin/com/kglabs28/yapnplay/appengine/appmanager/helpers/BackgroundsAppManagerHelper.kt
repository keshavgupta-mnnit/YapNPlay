package com.kglabs28.yapnplay.appengine.appmanager.helpers

import com.kglabs28.yapnplay.appengine.appmanager.AppManager
import com.kglabs28.yapnplay.appengine.appmanager.AppManagerKeys

/** Background and art image URLs; "" keeps the built-in image. */
class BackgroundsAppManagerHelper(private val appManager: AppManager) {
    fun home(): String = appManager.get(AppManagerKeys.Backgrounds.HOME)
    fun leaderboard(): String = appManager.get(AppManagerKeys.Backgrounds.LEADERBOARD)
    fun gameZoneCard(): String = appManager.get(AppManagerKeys.Backgrounds.GAME_ZONE_CARD)
    fun friendZoneCard(): String = appManager.get(AppManagerKeys.Backgrounds.FRIEND_ZONE_CARD)
    fun royalLudoArt(): String = appManager.get(AppManagerKeys.Backgrounds.ROYAL_LUDO_ART)
    fun cityBazaarArt(): String = appManager.get(AppManagerKeys.Backgrounds.CITY_BAZAAR_ART)
    fun monoArt(): String = appManager.get(AppManagerKeys.Backgrounds.MONO_ART)
    fun monoChaosArt(): String = appManager.get(AppManagerKeys.Backgrounds.MONO_CHAOS_ART)
    fun doodleKingArt(): String = appManager.get(AppManagerKeys.Backgrounds.DOODLE_KING_ART)
}
