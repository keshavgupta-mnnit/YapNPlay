package com.kglabs28.yapnplay.appengine.features.matchmaking.usecase

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.features.matchmaking.repository.MatchmakingRepository

/** How many players are online right now (Game Zone pill). */
class GetOnlinePlayerCountUseCase(private val repository: MatchmakingRepository) {

    suspend operator fun invoke(): AppResult<Int> = repository.onlinePlayerCount()
}
