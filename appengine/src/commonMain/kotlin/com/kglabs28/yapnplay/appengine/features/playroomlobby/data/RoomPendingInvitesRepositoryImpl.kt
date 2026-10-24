package com.kglabs28.yapnplay.appengine.features.playroomlobby.data

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.core.common.CrashReporter
import com.kglabs28.yapnplay.appengine.core.common.DispatcherProvider
import com.kglabs28.yapnplay.appengine.core.common.YapNPlayError
import com.kglabs28.yapnplay.appengine.core.network.BackendPaths
import com.kglabs28.yapnplay.appengine.features.playroomlobby.repository.RoomPendingInvitesRepository
import com.kglabs28.yapnplay.appengine.protocol.RoomPendingInvites
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.withContext

/** Reads `GET /rooms/{roomId}/invites`. */
class RoomPendingInvitesRepositoryImpl(
    private val client: HttpClient,
    private val dispatchers: DispatcherProvider,
) : RoomPendingInvitesRepository {
    override suspend fun pendingUserIds(roomId: String): AppResult<Set<String>> = withContext(dispatchers.io) {
        try {
            val pending: RoomPendingInvites = client.get(BackendPaths.roomPendingInvites(roomId)).body()
            AppResult.Success(pending.userIds.toSet())
        } catch (e: Exception) {
            CrashReporter.record(e, "RoomPendingInvitesRepository.pendingUserIds")
            AppResult.Failure(YapNPlayError.Network("pending invites failed"))
        }
    }
}
