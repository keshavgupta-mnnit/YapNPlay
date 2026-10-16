package com.kglabs28.yapnplay.appengine.features.matchmaking.data.remote

import com.kglabs28.yapnplay.appengine.protocol.OnlineStats
import kotlinx.coroutines.flow.Flow

/**
 * Quick Play transport. [queue] sends `JoinQueue(gameId)` on the
 * MATCHMAKING channel and streams the replies until `MatchFound` or
 * `QueueCancelled`; cancelling the collection must send `LeaveQueue`.
 */
interface MatchmakingClient {
    fun queue(gameId: String): Flow<MatchmakingMessage>

    /** Sends `LeaveQueue`. Safe to call when not queued. */
    suspend fun leaveQueue()

    /** `GET /stats/online` */
    suspend fun onlineStats(): OnlineStats
}
