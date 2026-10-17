package com.kglabs28.yapnplay.appengine.features.moderation.usecase

import com.kglabs28.yapnplay.appengine.features.moderation.domain.ModerationRepository
import com.kglabs28.yapnplay.appengine.core.common.AppResult

/** Watch a rewarded ad to lift the block early. */
class ClearPenaltyWithAdUseCase(private val repository: ModerationRepository) {
    suspend operator fun invoke(): AppResult<Unit> = repository.clearPenaltyWithAd()
}
