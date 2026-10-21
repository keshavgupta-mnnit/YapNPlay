package com.kglabs28.yapnplay.appengine.appmanager.helpers

import com.kglabs28.yapnplay.appengine.appmanager.AppManager
import com.kglabs28.yapnplay.appengine.appmanager.AppManagerKeys

/** City Bazaar table pacing and messages. */
class CityBazaarAppManagerHelper(private val appManager: AppManager) {
    fun moneyPopupsDefault(): Boolean = appManager.get(AppManagerKeys.CityBazaar.MONEY_POPUPS_DEFAULT)
    fun eventBreathMs(): Long = appManager.get(AppManagerKeys.CityBazaar.EVENT_BREATH_MS)
    fun eventBreathMaxMs(): Long = appManager.get(AppManagerKeys.CityBazaar.EVENT_BREATH_MAX_MS)
    fun popupMs(): Long = appManager.get(AppManagerKeys.CityBazaar.POPUP_MS)
    fun snackbarMs(): Long = appManager.get(AppManagerKeys.CityBazaar.SNACKBAR_MS)
}
