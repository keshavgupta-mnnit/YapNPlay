package com.kglabs28.yapnplay.appengine.features.matchmaking.repository

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.features.matchmaking.domain.MatchmakingStatus
import kotlinx.coroutines.flow.Flow

/**
 * Public Quick Play. [findMatch] streams the search until a match is
 * found or the search ends; stop collecting to leave the queue.
 */
interface MatchmakingRepository {
    fun findMatch(gameId: String): Flow<MatchmakingStatus>
    suspend fun leaveQueue()
    suspend fun onlinePlayerCount(): AppResult<Int>
}
