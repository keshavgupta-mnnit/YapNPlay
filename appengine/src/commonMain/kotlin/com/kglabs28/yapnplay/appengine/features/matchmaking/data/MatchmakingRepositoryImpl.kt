package com.kglabs28.yapnplay.appengine.features.matchmaking.data

import com.kglabs28.yapnplay.appengine.core.common.AppClock
import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.core.common.CrashReporter
import com.kglabs28.yapnplay.appengine.core.common.DispatcherProvider
import com.kglabs28.yapnplay.appengine.core.common.YapNPlayError
import com.kglabs28.yapnplay.appengine.features.matchmaking.data.remote.MatchmakingClient
import com.kglabs28.yapnplay.appengine.features.matchmaking.data.remote.MatchmakingMessage
import com.kglabs28.yapnplay.appengine.features.matchmaking.domain.ActiveMatchInfo
import com.kglabs28.yapnplay.appengine.features.matchmaking.domain.MatchmakingCancelReason
import com.kglabs28.yapnplay.appengine.features.matchmaking.domain.MatchmakingStatus
import com.kglabs28.yapnplay.appengine.features.matchmaking.repository.MatchmakingRepository
import com.kglabs28.yapnplay.appengine.protocol.QueueCancelReason
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.milliseconds

/**
 * Maps matchmaking-channel messages to domain status. Server deadlines
 * arrive as absolute server time; they are re-based on this device's
 * clock (deadline − serverTime + now) so a skewed phone clock still
 * shows the right countdown. Never throws: failures end the search.
 */
class MatchmakingRepositoryImpl(
    private val client: MatchmakingClient,
    private val clock: AppClock,
    private val dispatchers: DispatcherProvider,
) : MatchmakingRepository {

    override fun findMatch(gameId: String): Flow<MatchmakingStatus> =
        client.queue(gameId)
            .map { message -> message.toDomain(gameId) }
            .catch { e ->
                CrashReporter.record(e, "MatchmakingRepository.findMatch:$gameId")
                emit(MatchmakingStatus.Cancelled(MatchmakingCancelReason.CONNECTION_FAILED))
            }
            .flowOn(dispatchers.io)

    override suspend fun leaveQueue() = withContext(dispatchers.io) {
        try {
            client.leaveQueue()
        } catch (e: Exception) {
            CrashReporter.record(e, "MatchmakingRepository.leaveQueue")
        }
    }

    override suspend fun onlinePlayerCount(): AppResult<Int> = withContext(dispatchers.io) {
        try {
            AppResult.Success(client.onlineStats().online)
        } catch (e: Exception) {
            CrashReporter.record(e, "MatchmakingRepository.onlinePlayerCount")
            AppResult.Failure(YapNPlayError.Network("online stats failed"))
        }
    }

    override suspend fun activeMatch(): AppResult<ActiveMatchInfo?> = withContext(dispatchers.io) {
        try {
            AppResult.Success(client.activeMatch()?.let { ActiveMatchInfo(it.matchId, it.gameId, it.title) })
        } catch (e: Exception) {
            CrashReporter.record(e, "MatchmakingRepository.activeMatch")
            AppResult.Failure(YapNPlayError.Network("active match failed"))
        }
    }

    private fun MatchmakingMessage.toDomain(gameId: String): MatchmakingStatus = when (this) {
        is MatchmakingMessage.Status -> MatchmakingStatus.Searching(
            gameId = gameId,
            playersFound = status.playersFound,
            playersNeeded = status.playersNeeded,
            searchEndsAt = clock.now() + (status.searchDeadline - status.serverTime).milliseconds,
        )
        is MatchmakingMessage.Found -> MatchmakingStatus.Found(
            matchId = found.matchId,
            humanPlayers = found.humanPlayers,
            cpuPlayers = found.cpuPlayers,
        )
        is MatchmakingMessage.Cancelled -> MatchmakingStatus.Cancelled(
            when (cancelled.reason) {
                QueueCancelReason.NOT_ENOUGH_PLAYERS -> MatchmakingCancelReason.NOT_ENOUGH_PLAYERS
                QueueCancelReason.GAME_UNAVAILABLE -> MatchmakingCancelReason.GAME_UNAVAILABLE
                QueueCancelReason.LEFT_QUEUE -> MatchmakingCancelReason.LEFT_QUEUE
            },
        )
    }
}
