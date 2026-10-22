package com.kglabs28.yapnplay.appengine.core.ads

/** No ads on desktop: nothing is ever watched. */
class NoRewardedAds : RewardedAds {
    override fun isReady(): Boolean = false
    override fun preload() = Unit
    override suspend fun show(): Boolean = false
}
