package com.kglabs28.yapnplay.appengine.features.matchhistory.data

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.core.common.CrashReporter
import com.kglabs28.yapnplay.appengine.core.common.DispatcherProvider
import com.kglabs28.yapnplay.appengine.core.common.YapNPlayError
import com.kglabs28.yapnplay.appengine.core.network.BackendPaths
import com.kglabs28.yapnplay.appengine.features.matchhistory.domain.MatchHistoryRepository
import com.kglabs28.yapnplay.appengine.features.matchhistory.domain.PastMatch
import com.kglabs28.yapnplay.appengine.features.matchhistory.domain.PastSeat
import com.kglabs28.yapnplay.appengine.features.matchresult.domain.SeatResult
import com.kglabs28.yapnplay.appengine.protocol.MatchHistoryResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.withContext

/** Reads `GET /matches/history` into [PastMatch]es. */
class MatchHistoryRepositoryImpl(
    private val client: HttpClient,
    private val dispatchers: DispatcherProvider,
) : MatchHistoryRepository {
    override suspend fun history(): AppResult<List<PastMatch>> = withContext(dispatchers.io) {
        try {
            val response: MatchHistoryResponse = client.get(BackendPaths.MATCH_HISTORY).body()
            AppResult.Success(
                response.matches.map { m ->
                    PastMatch(
                        matchId = m.matchId,
                        gameId = m.gameId,
                        roomMatch = m.roomMatch,
                        roomName = m.roomName,
                        startedAt = m.startedAt,
                        endedAt = m.endedAt,
                        mySeat = m.mySeat,
                        seats = m.seats.sortedBy { it.place }.map { s ->
                            PastSeat(s.seat, s.name, s.avatarId, s.place, s.userId == null, SeatResult(s.seat, s.coinsWon, s.completionCoins, s.xp, s.stake), left = s.left)
                        },
                    )
                },
            )
        } catch (e: Exception) {
            CrashReporter.record(e, "MatchHistoryRepository.history")
            AppResult.Failure(YapNPlayError.Network("match history failed"))
        }
    }
}
