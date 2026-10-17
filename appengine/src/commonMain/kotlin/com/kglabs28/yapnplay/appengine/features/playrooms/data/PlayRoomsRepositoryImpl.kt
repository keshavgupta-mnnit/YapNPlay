package com.kglabs28.yapnplay.appengine.features.playrooms.data

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.core.common.DispatcherProvider
import com.kglabs28.yapnplay.appengine.features.playrooms.data.local.PlayRoomsLocalManager
import com.kglabs28.yapnplay.appengine.features.playrooms.data.remote.PlayRoomsRemoteManager
import com.kglabs28.yapnplay.appengine.features.playrooms.domain.JoinOutcome
import com.kglabs28.yapnplay.appengine.features.playrooms.domain.PlayRoom
import com.kglabs28.yapnplay.appengine.features.playrooms.domain.PlayRoomPreview
import com.kglabs28.yapnplay.appengine.features.playrooms.repository.PlayRoomsRepository
import kotlinx.coroutines.withContext

/**
 * Network first, cache fallback: fresh data wins, offline still works.
 * Callers see only [PlayRoomsRepository]. Work runs on IO.
 */
class PlayRoomsRepositoryImpl(
    private val remote: PlayRoomsRemoteManager,
    private val local: PlayRoomsLocalManager,
    private val dispatchers: DispatcherProvider,
) : PlayRoomsRepository {

    override suspend fun getMyRooms(): AppResult<List<PlayRoom>> =
        withContext(dispatchers.io) {
            when (val result = remote.fetchMyRooms()) {
                is AppResult.Success -> {
                    local.saveRooms(result.data)
                    result
                }
                is AppResult.Failure -> {
                    val cached = local.rooms()
                    if (cached.isNotEmpty()) AppResult.Success(cached) else result
                }
            }
        }

    override suspend fun previewRoom(code: String): AppResult<PlayRoomPreview> =
        withContext(dispatchers.io) { remote.previewRoom(code) }

    override suspend fun joinRoom(code: String): AppResult<JoinOutcome> =
        withContext(dispatchers.io) {
            remote.joinRoom(code).also { result ->
                // A pending request is not a room yet; only a real join is cached.
                val joined = (result as? AppResult.Success)?.data as? JoinOutcome.Joined
                if (joined != null) local.addRoom(joined.room)
            }
        }
}
