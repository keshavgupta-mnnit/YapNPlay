package com.kglabs28.yapnplay.appengine.features.matchmaking.data.remote

import com.kglabs28.yapnplay.appengine.core.network.BackendPaths
import com.kglabs28.yapnplay.appengine.core.network.RealtimeConnection
import com.kglabs28.yapnplay.appengine.protocol.ActiveMatch
import com.kglabs28.yapnplay.appengine.protocol.JoinQueue
import com.kglabs28.yapnplay.appengine.protocol.LeaveQueue
import com.kglabs28.yapnplay.appengine.protocol.MatchFound
import com.kglabs28.yapnplay.appengine.protocol.OnlineStats
import com.kglabs28.yapnplay.appengine.protocol.QueueCancelled
import com.kglabs28.yapnplay.appengine.protocol.QueueStatus
import com.kglabs28.yapnplay.appengine.protocol.transport.Channel
import com.kglabs28.yapnplay.appengine.protocol.transport.MessageType
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.transformWhile
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Quick Play over the MATCHMAKING channel. `JoinQueue` gets an Ack; the
 * progress (`QueueStatus`) and the end (`MatchFound` / `QueueCancelled`)
 * arrive as pushes. Stopping the search early sends `LeaveQueue`.
 */
class MatchmakingClientImpl(
    private val client: HttpClient,
    private val realtime: RealtimeConnection,
) : MatchmakingClient {

    override fun queue(gameId: String, entryCoins: Int): Flow<MatchmakingMessage> = channelFlow {
        var finished = false
        try {
            // Listen first so the first QueueStatus (sent right after the Ack) is not missed.
            val listener = launch(start = CoroutineStart.UNDISPATCHED) {
                realtime.stream(Channel.MATCHMAKING)
                    .mapNotNull { envelope ->
                        when (envelope.type) {
                            MessageType.QUEUE_STATUS -> MatchmakingMessage.Status(realtime.decode(envelope, QueueStatus.serializer()))
                            MessageType.MATCH_FOUND -> MatchmakingMessage.Found(realtime.decode(envelope, MatchFound.serializer()))
                            MessageType.QUEUE_CANCELLED -> MatchmakingMessage.Cancelled(realtime.decode(envelope, QueueCancelled.serializer()))
                            else -> null
                        }
                    }
                    .transformWhile { emit(it); it is MatchmakingMessage.Status }
                    .collect { send(it) }
                finished = true
            }
            realtime.request(Channel.MATCHMAKING, MessageType.JOIN_QUEUE, JoinQueue.serializer(), JoinQueue(gameId, entryCoins))
            listener.join()
        } finally {
            if (!finished) withContext(NonCancellable) { runCatching { leaveQueue() } }
        }
    }

    override suspend fun leaveQueue() {
        realtime.request(Channel.MATCHMAKING, MessageType.LEAVE_QUEUE, LeaveQueue.serializer(), LeaveQueue)
    }

    override suspend fun onlineStats(): OnlineStats = client.get(BackendPaths.ONLINE_STATS).body()

    override suspend fun activeMatch(): ActiveMatch? {
        val response = client.get(BackendPaths.ACTIVE_MATCH)
        return if (response.status == HttpStatusCode.NoContent) null else response.body()
    }
}
