package com.kglabs28.yapnplay.appengine.core.ads

/**
 * A rewarded video ad (AdMob on Android). [show] loads and plays one and
 * suspends until it closes: true only when it was watched to the end, so
 * the reward may be claimed from the server.
 */
interface RewardedAds {
    suspend fun show(): Boolean
}
