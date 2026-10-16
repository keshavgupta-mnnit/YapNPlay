package com.kglabs28.yapnplay.appengine.features.matchmaking.data.remote

import com.kglabs28.yapnplay.appengine.protocol.MatchFound
import com.kglabs28.yapnplay.appengine.protocol.QueueCancelled
import com.kglabs28.yapnplay.appengine.protocol.QueueStatus

/**
 * The server-to-client matchmaking-channel messages, as one type so a
 * search can be a single stream. Payloads are the contract types.
 */
sealed interface MatchmakingMessage {
    data class Status(val status: QueueStatus) : MatchmakingMessage
    data class Found(val found: MatchFound) : MatchmakingMessage
    data class Cancelled(val cancelled: QueueCancelled) : MatchmakingMessage
}
