package com.kglabs28.yapnplay.appengine.core.ads

import kotlinx.coroutines.flow.StateFlow

/**
 * A rewarded video ad (AdMob on Android). One is kept loaded ahead of
 * time: [isReady] says whether one can play right now (no ad, no offer is
 * shown), [readiness] follows it as it changes, and [preload] fetches the next. [show] plays it (loading one
 * first if none is ready) and suspends until it closes: true only when it
 * was watched to the end, so the reward may be claimed from the server.
 */
interface RewardedAds {
    fun isReady(): Boolean
    val readiness: StateFlow<Boolean>
    fun preload()
    suspend fun show(): Boolean
}
