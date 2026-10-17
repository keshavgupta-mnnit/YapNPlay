package com.kglabs28.yapnplay.appengine.features.playroomlobby.data

import com.kglabs28.yapnplay.appengine.core.common.AppClock
import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.core.common.CrashReporter
import com.kglabs28.yapnplay.appengine.core.common.DispatcherProvider
import com.kglabs28.yapnplay.appengine.core.common.YapNPlayError
import com.kglabs28.yapnplay.appengine.features.playroomlobby.data.remote.LobbyRealtimeClient
import com.kglabs28.yapnplay.appengine.features.playroomlobby.domain.LobbyCommand
import com.kglabs28.yapnplay.appengine.features.playroomlobby.domain.LobbyRoom
import com.kglabs28.yapnplay.appengine.features.playroomlobby.repository.PlayRoomLobbyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * Maps the room channel to domain and sends commands. Never throws:
 * a failed command comes back as a typed error.
 */
class PlayRoomLobbyRepositoryImpl(
    private val client: LobbyRealtimeClient,
    private val clock: AppClock,
    private val dispatchers: DispatcherProvider,
) : PlayRoomLobbyRepository {

    override fun observeRoom(roomId: String): Flow<LobbyRoom> =
        client.observeRoom(roomId)
            .map { it.toLobbyRoom(clock.now()) }
            .flowOn(dispatchers.io)

    override fun observeSpeaking(roomId: String): Flow<Set<String>> =
        client.observeSpeaking(roomId).flowOn(dispatchers.io)

    override fun observeMatchStarting(roomId: String): Flow<String> =
        client.observeMatchStarting(roomId).map { it.matchId }.flowOn(dispatchers.io)

    override suspend fun send(roomId: String, command: LobbyCommand): AppResult<Unit> =
        withContext(dispatchers.io) {
            try {
                client.send(roomId, command)
                AppResult.Success(Unit)
            } catch (e: Exception) {
                CrashReporter.record(e, "PlayRoomLobbyRepository.send:${command::class.simpleName}")
                AppResult.Failure(YapNPlayError.Network("lobby command failed"))
            }
        }
}
