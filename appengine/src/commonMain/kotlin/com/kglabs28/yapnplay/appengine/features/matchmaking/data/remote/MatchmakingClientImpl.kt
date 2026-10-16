package com.kglabs28.yapnplay.appengine.features.matchmaking.data.remote

import com.kglabs28.yapnplay.appengine.core.common.AppClock
import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.common.FakeDataProvider
import com.kglabs28.yapnplay.appengine.features.gamecatalog.repository.GameCatalogRepository
import com.kglabs28.yapnplay.appengine.protocol.MatchFound
import com.kglabs28.yapnplay.appengine.protocol.OnlineStats
import com.kglabs28.yapnplay.appengine.protocol.QueueCancelReason
import com.kglabs28.yapnplay.appengine.protocol.QueueCancelled
import com.kglabs28.yapnplay.appengine.protocol.QueueStatus
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Quick Play client. TODAY: a fake server in [fakeQueue] that behaves
 * like the real one — search window, early start when every seat has a
 * human, CPU fill at the deadline — fed by [FakeDataProvider.Matchmaking].
 *
 * LATER (backend ready): uncomment the production paths. They need the
 * app-wide WebSocket connection manager (Phase 12); nothing above this
 * class changes.
 */
class MatchmakingClientImpl(
    private val client: HttpClient,
    private val catalog: GameCatalogRepository,
    private val clock: AppClock,
) : MatchmakingClient {

    override fun queue(gameId: String): Flow<MatchmakingMessage> {
        // --- production path (uncomment when backend is live) ---
        // return realtime.stream(Channel.MATCHMAKING)
        //     .onStart { realtime.send(Channel.MATCHMAKING, MessageType.JOIN_QUEUE, JoinQueue(gameId)) }
        //     .mapNotNull { envelope ->
        //         when (envelope.type) {
        //             MessageType.QUEUE_STATUS -> MatchmakingMessage.Status(envelope.decode())
        //             MessageType.MATCH_FOUND -> MatchmakingMessage.Found(envelope.decode())
        //             MessageType.QUEUE_CANCELLED -> MatchmakingMessage.Cancelled(envelope.decode())
        //             else -> null
        //         }
        //     }
        //     .transformWhile { emit(it); it is MatchmakingMessage.Status }
        //     .onCompletion { cause -> if (cause != null) leaveQueue() }

        // --- fake path (delete when switching to production) ---
        return fakeQueue(gameId)
    }

    override suspend fun leaveQueue() {
        // --- production path (uncomment when backend is live) ---
        // realtime.send(Channel.MATCHMAKING, MessageType.LEAVE_QUEUE, LeaveQueue)

        // --- fake path: the fake server stops when its stream is cancelled ---
    }

    override suspend fun onlineStats(): OnlineStats {
        // --- production path (uncomment when backend is live) ---
        // return client.get(AppConstants.SERVER_BASE_URL + "/stats/online").body()

        // --- fake path (delete when switching to production) ---
        delay(FakeDataProvider.NETWORK_DELAY_MS)
        return FakeDataProvider.Matchmaking.onlineStats
    }

    /**
     * Fake server: you are player one; others arrive on the fake schedule.
     * All Quick Play seats taken → start now. Deadline → CPU fills the rest.
     */
    private fun fakeQueue(gameId: String): Flow<MatchmakingMessage> = flow {
        val game = catalog.findGame(gameId)
        if (game == null) {
            emit(MatchmakingMessage.Cancelled(QueueCancelled(gameId, QueueCancelReason.GAME_UNAVAILABLE)))
            return@flow
        }
        val fake = FakeDataProvider.Matchmaking
        val startedAt = clock.now().toEpochMilliseconds()
        val deadline = startedAt + fake.SEARCH_SECONDS * MILLIS_PER_SECOND
        while (true) {
            val now = clock.now().toEpochMilliseconds()
            val arrived = fake.otherPlayersJoinAfterMs.count { now - startedAt >= it }
            val seats = game.quickPlaySeats
            val found = (1 + arrived).coerceAtMost(seats)
            if (found >= seats || now >= deadline) {
                val matchFound = MatchFound(
                    matchId = "match-$gameId-$startedAt",
                    gameId = gameId,
                    humanPlayers = found,
                    cpuPlayers = seats - found,
                )
                emit(MatchmakingMessage.Found(matchFound))
                return@flow
            }
            emit(MatchmakingMessage.Status(QueueStatus(gameId, found, seats, deadline, now)))
            delay(fake.STATUS_INTERVAL_MS)
        }
    }

    private companion object {
        const val MILLIS_PER_SECOND = 1_000L
    }
}
