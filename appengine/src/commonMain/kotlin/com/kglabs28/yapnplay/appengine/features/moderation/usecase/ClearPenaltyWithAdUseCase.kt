package com.kglabs28.yapnplay.appengine.features.moderation.usecase

import com.kglabs28.yapnplay.appengine.core.ads.RewardedAds
import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.core.common.YapNPlayError
import com.kglabs28.yapnplay.appengine.features.moderation.domain.ModerationRepository

/** Watch a rewarded ad to the end to lift the block early. */
class ClearPenaltyWithAdUseCase(
    private val ads: RewardedAds,
    private val repository: ModerationRepository,
) {
    suspend operator fun invoke(): AppResult<Unit> {
        if (!ads.show()) return AppResult.Failure(YapNPlayError.Unknown("ad not watched"))
        return repository.clearPenaltyWithAd()
    }
}
