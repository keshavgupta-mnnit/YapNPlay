package com.kglabs28.yapnplay.appengine.features.coins.usecase

import kotlinx.coroutines.delay
import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.common.AppLog
import com.kglabs28.yapnplay.appengine.core.ads.RewardedAds
import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.core.common.YapNPlayError
import com.kglabs28.yapnplay.appengine.features.coins.domain.AdCoinsReward
import com.kglabs28.yapnplay.appengine.features.coins.domain.CoinsRepository
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * Plays a rewarded ad and, only when it was watched to the end, asks the
 * server for the coins (each view has its own id, so it pays once). An
 * ad that doesn't arrive within [AD_TIMEOUT] counts as no ad.
 */
class WatchAdForCoinsUseCase(
    private val ads: RewardedAds,
    private val repository: CoinsRepository,
) {
    @OptIn(ExperimentalUuidApi::class)
    suspend operator fun invoke(): AppResult<AdCoinsReward> {
        // No overall time limit: the ad decides how long it plays (loading has its own limit).
        val watched = ads.show()
        AppLog.d(AppConstants.LOG_TAG_ADS, "watch: ad result=$watched")
        if (!watched) return AppResult.Failure(YapNPlayError.Unknown("ad not watched"))
        // The ad was watched: the coins must arrive. One id for every try, so the server pays it once.
        val adId = Uuid.random().toString()
        var result = repository.claimAdReward(adId)
        var tries = 1
        while (result is AppResult.Failure && result.error !is YapNPlayError.LimitReached && tries < CLAIM_TRIES) {
            delay(CLAIM_RETRY_MS * tries)
            tries++
            result = repository.claimAdReward(adId)
        }
        AppLog.d(AppConstants.LOG_TAG_ADS, "watch: reward claim=${if (result is AppResult.Success) "paid" else "failed: ${(result as? AppResult.Failure)?.error}"} after $tries tries")
        return result
    }
}

/** Tries to claim a watched ad's coins, and the wait before each retry (× the try). */
private const val CLAIM_TRIES = 3
private const val CLAIM_RETRY_MS = 1_500L
