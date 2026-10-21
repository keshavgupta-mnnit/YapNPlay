package com.kglabs28.yapnplay.appengine.features.matchmaking.usecase

import com.kglabs28.yapnplay.appengine.features.matchmaking.domain.MatchmakingStatus
import com.kglabs28.yapnplay.appengine.features.matchmaking.repository.MatchmakingRepository
import kotlinx.coroutines.flow.Flow

/**
 * Starts a public Quick Play search for one game. The stream ends with
 * Found or Cancelled; cancelling the collector leaves the queue.
 */
class FindQuickPlayMatchUseCase(private val repository: MatchmakingRepository) {

    operator fun invoke(gameId: String, entryCoins: Int): Flow<MatchmakingStatus> = repository.findMatch(gameId, entryCoins)
}
