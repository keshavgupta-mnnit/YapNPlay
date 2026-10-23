package com.kglabs28.yapnplay.appengine.features.coins.usecase

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.features.coins.domain.AdLimitStatus
import com.kglabs28.yapnplay.appengine.features.coins.domain.CoinsRepository

/** How many free-coin ads are left today and when the next one can pay. */
class GetAdLimitStatusUseCase(private val repository: CoinsRepository) {
    suspend operator fun invoke(): AppResult<AdLimitStatus> = repository.adStatus()
}
