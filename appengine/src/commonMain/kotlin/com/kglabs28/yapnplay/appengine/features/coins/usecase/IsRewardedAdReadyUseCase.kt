    package com.kglabs28.yapnplay.appengine.features.coins.usecase

import kotlinx.coroutines.flow.StateFlow
import com.kglabs28.yapnplay.appengine.core.ads.RewardedAds
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Duration

/**
 * Whether a rewarded ad can play right now (ads load only at launch and
 * after one plays, never on asking). A free-coins offer with no ad behind
 * it is never shown. [await] waits a little for the launch ad that is
 * still loading (the app's launch offer).
 */
class IsRewardedAdReadyUseCase(private val ads: RewardedAds) {
    operator fun invoke(): Boolean = ads.isReady()

    /** The app's one ad-ready state, as it changes: every open coins offer follows it. */
    val changes: StateFlow<Boolean> get() = ads.readiness

    suspend fun await(timeout: Duration): Boolean {
        if (invoke()) return true
        // Starts a fresh load if none is on its way (an earlier one may have failed).
        ads.preload()
        return withTimeoutOrNull(timeout) { ads.readiness.first { it } } == true
    }
}
