    package com.kglabs28.yapnplay.appengine.features.coins.usecase

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

    suspend fun await(timeout: Duration): Boolean {
        if (invoke()) return true
        return withTimeoutOrNull(timeout) { ads.readiness.first { it } } == true
    }
}
