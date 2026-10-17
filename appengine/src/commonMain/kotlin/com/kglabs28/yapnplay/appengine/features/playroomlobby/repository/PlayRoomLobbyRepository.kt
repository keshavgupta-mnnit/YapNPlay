package com.kglabs28.yapnplay.appengine.features.playroomlobby.repository

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.features.playroomlobby.domain.LobbyCommand
import com.kglabs28.yapnplay.appengine.features.playroomlobby.domain.LobbyRoom
import kotlinx.coroutines.flow.Flow

/** A play room's live lobby: state in, commands out. */
interface PlayRoomLobbyRepository {
    fun observeRoom(roomId: String): Flow<LobbyRoom>
    fun observeSpeaking(roomId: String): Flow<Set<String>>

    /** Match ids as the server starts matches for this room. */
    fun observeMatchStarting(roomId: String): Flow<String>
    suspend fun send(roomId: String, command: LobbyCommand): AppResult<Unit>
}
