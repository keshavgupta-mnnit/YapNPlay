package com.kglabs28.yapnplay.appengine.appmanager.helpers

import com.kglabs28.yapnplay.appengine.core.common.BuildInfo
import com.kglabs28.yapnplay.appengine.appmanager.AppManager
import com.kglabs28.yapnplay.appengine.appmanager.AppManagerKeys

/** Ads: the on/off switches and the AdMob units (rewarded, and the native ad pill). */
class AdsAppManagerHelper(private val appManager: AppManager) {
    /** On in debug builds (to test ads), else as `ads_enabled` says. */
    fun isEnabled(): Boolean = BuildInfo.isDebug || appManager.get(AppManagerKeys.Ads.ENABLED)
    /** The free-coins offer when the app opens (only while ads are on). */
    fun isLaunchOfferEnabled(): Boolean = isEnabled() && appManager.get(AppManagerKeys.Ads.LAUNCH_OFFER)
    fun rewardedUnitId(): String = appManager.get(AppManagerKeys.Ads.REWARDED_UNIT_ID)
    /** The ad pill over the game's top bar and under the connecting card (only while ads are on). */
    fun isNativeEnabled(): Boolean = isEnabled() && appManager.get(AppManagerKeys.Ads.NATIVE_ENABLED)
    fun nativeUnitId(): String = appManager.get(AppManagerKeys.Ads.NATIVE_UNIT_ID)
    /** How long an all-game ad pill keeps one ad before the next (never under [MIN_REFRESH_MS]). */
    fun nativeRefreshMs(): Long = (appManager.get(AppManagerKeys.Ads.NATIVE_REFRESH_SECONDS) * MILLIS_PER_SECOND).coerceAtLeast(MIN_REFRESH_MS)

    private companion object {
        const val MILLIS_PER_SECOND = 1_000L
        /** A mistyped 0 or 1 never turns into an ad request every frame. */
        const val MIN_REFRESH_MS = 10_000L
    }
}
