package com.kglabs28.yapnplay.appengine.features.coins.usecase

import com.kglabs28.yapnplay.appengine.core.ads.RewardedAds
import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.core.common.YapNPlayError
import com.kglabs28.yapnplay.appengine.features.coins.domain.AdCoinsReward
import com.kglabs28.yapnplay.appengine.features.coins.domain.CoinsRepository
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * Plays a rewarded ad and, only when it was watched to the end, asks the
 * server for the coins (each view has its own id, so it pays once).
 */
class WatchAdForCoinsUseCase(
    private val ads: RewardedAds,
    private val repository: CoinsRepository,
) {
    @OptIn(ExperimentalUuidApi::class)
    suspend operator fun invoke(): AppResult<AdCoinsReward> {
        if (!ads.show()) return AppResult.Failure(YapNPlayError.Unknown("ad not watched"))
        return repository.claimAdReward(Uuid.random().toString())
    }
}
