package com.kglabs28.yapnplay.appengine.features.playroomlobby.data.remote

import com.kglabs28.yapnplay.appengine.features.playroomlobby.domain.LobbyCommand
import com.kglabs28.yapnplay.appengine.protocol.MatchStarting
import com.kglabs28.yapnplay.appengine.protocol.RoomSnapshot
import kotlinx.coroutines.flow.Flow

/**
 * Lobby transport: the ROOM channel of the WebSocket (snapshots in,
 * commands out) plus who is talking right now (from the voice provider).
 */
interface LobbyRealtimeClient {
    /** Sends `JoinRoom(code)`/presence and streams every `RoomSnapshot`. */
    fun observeRoom(roomId: String): Flow<RoomSnapshot>

    /** `MatchStarting` for this room: open the match screen. */
    fun observeMatchStarting(roomId: String): Flow<MatchStarting>

    /** User ids currently speaking (LiveKit active speakers later). */
    fun observeSpeaking(roomId: String): Flow<Set<String>>

    suspend fun send(roomId: String, command: LobbyCommand)
}
