    package com.kglabs28.yapnplay.appengine.features.coins.usecase

import com.kglabs28.yapnplay.appengine.core.ads.RewardedAds

/**
 * Whether a rewarded ad can play right now; when not, it starts loading one
 * so the next ask may find it ready. A free-coins offer with no ad behind it
 * is never shown.
 */
class IsRewardedAdReadyUseCase(private val ads: RewardedAds) {
    operator fun invoke(): Boolean = ads.isReady().also { ready -> if (!ready) ads.preload() }
}
