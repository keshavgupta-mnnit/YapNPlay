package com.kglabs28.yapnplay.appengine.features.playroomlobby.data.remote

import com.kglabs28.yapnplay.appengine.features.playroomlobby.domain.LobbyCommand
import com.kglabs28.yapnplay.appengine.protocol.MatchStarting
import com.kglabs28.yapnplay.appengine.protocol.RoomSnapshot
import kotlinx.coroutines.flow.Flow

/**
 * Lobby client. TODAY: every call goes to [FakeLobbyServer], which keeps
 * a room in memory and applies the same rules the real server will.
 *
 * LATER (backend ready): uncomment the production paths (they need the
 * app-wide WebSocket connection manager, report Phase 12, and the voice
 * client, Phase 13) and delete the fake lines. Nothing above changes.
 */
class LobbyRealtimeClientImpl(
    private val fakeServer: FakeLobbyServer,
) : LobbyRealtimeClient {

    override fun observeRoom(roomId: String): Flow<RoomSnapshot> {
        // --- production path ---
        // return realtime.stream(Channel.ROOM)
        //     .onStart { realtime.send(Channel.ROOM, MessageType.REQUEST_SNAPSHOT, RequestSnapshot(lastVersion = 0)) }
        //     .filter { it.type == MessageType.ROOM_SNAPSHOT }
        //     .map { it.decode<RoomSnapshot>() }
        //     .filter { it.room.id == roomId }

        // --- fake path ---
        return fakeServer.room(roomId)
    }

    override fun observeMatchStarting(roomId: String): Flow<MatchStarting> {
        // --- production path ---
        // return realtime.stream(Channel.ROOM)
        //     .filter { it.type == MessageType.MATCH_STARTING }
        //     .map { it.decode<MatchStarting>() }

        // --- fake path ---
        return fakeServer.matchStarting
    }

    override fun observeSpeaking(roomId: String): Flow<Set<String>> {
        // --- production path ---
        // return voiceClient.activeSpeakers

        // --- fake path ---
        return fakeServer.speaking(roomId)
    }

    override suspend fun send(roomId: String, command: LobbyCommand) {
        // --- production path: one room-channel message per command ---
        // when (command) {
        //     is LobbyCommand.SelectGame -> realtime.request(Channel.ROOM, MessageType.SELECT_GAME, SelectGame(command.gameId))
        //     is LobbyCommand.SetHouseRule -> realtime.request(Channel.ROOM, MessageType.SET_HOUSE_RULE, SetHouseRule(command.key, command.enabled))
        //     is LobbyCommand.SetBotFill -> realtime.request(Channel.ROOM, MessageType.SET_BOT_FILL, SetBotFill(command.enabled))
        //     is LobbyCommand.SetReady -> realtime.request(Channel.ROOM, MessageType.SET_READY, SetReady(command.ready))
        //     is LobbyCommand.SetWatching -> realtime.request(Channel.ROOM, MessageType.SET_WATCHING, SetWatching(command.watching))
        //     is LobbyCommand.SetVoiceState -> realtime.request(Channel.ROOM, MessageType.SET_VOICE_STATE, SetVoiceState(command.micOn, command.speakerOn))
        //     LobbyCommand.StartCountdown -> realtime.request(Channel.ROOM, MessageType.START_COUNTDOWN, StartCountdown)
        //     LobbyCommand.CancelCountdown -> realtime.request(Channel.ROOM, MessageType.CANCEL_COUNTDOWN, CancelCountdown)
        //     is LobbyCommand.TransferLeader -> realtime.request(Channel.ROOM, MessageType.TRANSFER_LEADER, TransferLeader(command.userId))
        //     is LobbyCommand.TransferAdmin -> realtime.request(Channel.ROOM, MessageType.TRANSFER_ADMIN, TransferAdmin(command.userId))
        //     is LobbyCommand.SetFriend -> if (command.friend) http.post("/friends/${command.userId}") else http.delete("/friends/${command.userId}")
        //     LobbyCommand.ExitLobby -> realtime.request(Channel.ROOM, MessageType.EXIT_LOBBY, ExitLobby)
        //     LobbyCommand.LeaveRoom -> realtime.request(Channel.ROOM, MessageType.LEAVE_ROOM, LeaveRoom)
        // }

        // --- fake path ---
        fakeServer.handle(roomId, command)
    }
}
