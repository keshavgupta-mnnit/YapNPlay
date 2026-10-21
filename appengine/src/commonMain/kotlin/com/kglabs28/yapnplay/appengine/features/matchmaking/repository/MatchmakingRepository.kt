package com.kglabs28.yapnplay.appengine.features.matchmaking.repository

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.features.matchmaking.domain.ActiveMatchInfo
import com.kglabs28.yapnplay.appengine.features.matchmaking.domain.MatchmakingStatus
import kotlinx.coroutines.flow.Flow

/**
 * Public Quick Play. [findMatch] streams the search until a match is
 * found or the search ends; stop collecting to leave the queue.
 */
interface MatchmakingRepository {
    /** [entryCoins]: the league's fee (0 = Starter, free). */
    fun findMatch(gameId: String, entryCoins: Int): Flow<MatchmakingStatus>
    suspend fun leaveQueue()
    suspend fun onlinePlayerCount(): AppResult<Int>
    /** The unfinished match the user can rejoin, or null. */
    suspend fun activeMatch(): AppResult<ActiveMatchInfo?>
}
