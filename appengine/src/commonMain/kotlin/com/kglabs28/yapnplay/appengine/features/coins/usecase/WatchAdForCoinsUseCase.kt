package com.kglabs28.yapnplay.appengine.features.coins.usecase

import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.common.AppLog
import com.kglabs28.yapnplay.appengine.core.ads.RewardedAds
import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.core.common.YapNPlayError
import com.kglabs28.yapnplay.appengine.features.coins.domain.AdCoinsReward
import com.kglabs28.yapnplay.appengine.features.coins.domain.CoinsRepository
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Duration.Companion.seconds
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
        val watched = withTimeoutOrNull(AD_TIMEOUT) { ads.show() }
        AppLog.d(AppConstants.LOG_TAG_ADS, "watch: ad result=${watched ?: "timed out"}")
        if (watched != true) return AppResult.Failure(YapNPlayError.Unknown("ad not watched"))
        return repository.claimAdReward(Uuid.random().toString()).also {
            AppLog.d(AppConstants.LOG_TAG_ADS, "watch: reward claim=${if (it is AppResult.Success) "paid" else "failed: ${(it as? AppResult.Failure)?.error}"}")
        }
    }
}

/** How long to wait for an ad to load and play before telling the user there's none. */
private val AD_TIMEOUT = 90.seconds
