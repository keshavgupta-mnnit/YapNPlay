package com.kglabs28.yapnplay.appengine.features.matchmaking.usecase

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.features.matchmaking.domain.ActiveMatchInfo
import com.kglabs28.yapnplay.appengine.features.matchmaking.repository.MatchmakingRepository

/** The unfinished match the user can rejoin, or null. */
class GetActiveMatchUseCase(private val repository: MatchmakingRepository) {

    suspend operator fun invoke(): AppResult<ActiveMatchInfo?> = repository.activeMatch()
}
