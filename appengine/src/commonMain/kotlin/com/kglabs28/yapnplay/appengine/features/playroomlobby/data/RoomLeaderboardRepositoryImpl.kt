package com.kglabs28.yapnplay.appengine.features.playroomlobby.data

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.core.common.CrashReporter
import com.kglabs28.yapnplay.appengine.core.common.DispatcherProvider
import com.kglabs28.yapnplay.appengine.core.common.YapNPlayError
import com.kglabs28.yapnplay.appengine.core.network.BackendPaths
import com.kglabs28.yapnplay.appengine.features.playroomlobby.domain.RoomMemberRecord
import com.kglabs28.yapnplay.appengine.features.playroomlobby.repository.RoomLeaderboardRepository
import com.kglabs28.yapnplay.appengine.protocol.RoomLeaderboard
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.withContext

/** Reads `GET /rooms/{roomId}/leaderboard` into [RoomMemberRecord]s. */
class RoomLeaderboardRepositoryImpl(
    private val client: HttpClient,
    private val dispatchers: DispatcherProvider,
) : RoomLeaderboardRepository {
    override suspend fun records(roomId: String): AppResult<List<RoomMemberRecord>> = withContext(dispatchers.io) {
        try {
            val board: RoomLeaderboard = client.get(BackendPaths.roomLeaderboard(roomId)).body()
            AppResult.Success(board.standings.map { RoomMemberRecord(it.userId, it.displayName, it.avatarId, it.played, it.wins, it.coins) })
        } catch (e: Exception) {
            CrashReporter.record(e, "RoomLeaderboardRepository.records")
            AppResult.Failure(YapNPlayError.Network("room leaderboard failed"))
        }
    }
}
