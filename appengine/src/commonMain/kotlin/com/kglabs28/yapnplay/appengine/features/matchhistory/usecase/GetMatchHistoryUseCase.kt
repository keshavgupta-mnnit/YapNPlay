package com.kglabs28.yapnplay.appengine.features.matchhistory.usecase

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.features.matchhistory.domain.MatchHistoryRepository
import com.kglabs28.yapnplay.appengine.features.matchhistory.domain.PastMatch

/** The user's latest finished matches, newest first. */
class GetMatchHistoryUseCase(private val repository: MatchHistoryRepository) {
    suspend operator fun invoke(): AppResult<List<PastMatch>> = repository.history()
}
