package com.kglabs28.yapnplay.appengine.core.ads

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/** No ads on desktop: nothing is ever watched. */
class NoRewardedAds : RewardedAds {
    override fun isReady(): Boolean = false
    override val readiness: StateFlow<Boolean> = MutableStateFlow(false)
    override fun preload() = Unit
    override suspend fun show(): Boolean = false
}
