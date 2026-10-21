package com.kglabs28.yapnplay.appengine.appmanager.helpers

import com.kglabs28.yapnplay.appengine.appmanager.AppManager
import com.kglabs28.yapnplay.appengine.appmanager.AppManagerKeys

/** Rewarded ads: the on/off switch and the AdMob unit. */
class AdsAppManagerHelper(private val appManager: AppManager) {
    fun isEnabled(): Boolean = appManager.get(AppManagerKeys.Ads.ENABLED)
    fun rewardedUnitId(): String = appManager.get(AppManagerKeys.Ads.REWARDED_UNIT_ID)
}
