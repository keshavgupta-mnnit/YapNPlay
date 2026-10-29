package com.kglabs28.yapnplay.appengine.features.playroomlobby.data.remote

import com.kglabs28.yapnplay.appengine.protocol.SetLeague
import com.kglabs28.yapnplay.appengine.core.common.CrashReporter
import com.kglabs28.yapnplay.appengine.core.network.RealtimeGateway
import com.kglabs28.yapnplay.appengine.features.playroomlobby.domain.LobbyCommand
import com.kglabs28.yapnplay.appengine.features.playrooms.data.remote.PlayRoomsApi
import com.kglabs28.yapnplay.appengine.features.profile.data.remote.ProfileApi
import com.kglabs28.yapnplay.appengine.features.voice.data.remote.VoiceClient
import com.kglabs28.yapnplay.appengine.features.voice.data.remote.VoiceTarget
import com.kglabs28.yapnplay.appengine.protocol.ApproveJoin
import com.kglabs28.yapnplay.appengine.protocol.CancelCountdown
import com.kglabs28.yapnplay.appengine.protocol.DeclineJoin
import com.kglabs28.yapnplay.appengine.protocol.ExitLobby
import com.kglabs28.yapnplay.appengine.protocol.JoinRoom
import com.kglabs28.yapnplay.appengine.protocol.LeaveRoom
import com.kglabs28.yapnplay.appengine.protocol.MatchStarting
import com.kglabs28.yapnplay.appengine.protocol.RemoveMember
import com.kglabs28.yapnplay.appengine.protocol.RenameRoom
import com.kglabs28.yapnplay.appengine.protocol.RoomSnapshot
import com.kglabs28.yapnplay.appengine.protocol.SelectGame
import com.kglabs28.yapnplay.appengine.protocol.SendFriendRequestBody
import com.kglabs28.yapnplay.appengine.protocol.SetBotFill
import com.kglabs28.yapnplay.appengine.protocol.SetHouseRule
import com.kglabs28.yapnplay.appengine.protocol.SetReady
import com.kglabs28.yapnplay.appengine.protocol.SetVoiceState
import com.kglabs28.yapnplay.appengine.protocol.SetWatching
import com.kglabs28.yapnplay.appengine.protocol.StartCountdown
import com.kglabs28.yapnplay.appengine.protocol.TransferAdmin
import com.kglabs28.yapnplay.appengine.protocol.TransferLeader
import com.kglabs28.yapnplay.appengine.protocol.transport.Channel
import com.kglabs28.yapnplay.appengine.protocol.transport.MessageType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.KSerializer

/**
 * Lobby over the ROOM channel. Opening a room enters it (`JoinRoom(code)`),
 * again after every reconnect because presence ends when the socket drops;
 * after that every `RoomSnapshot` for this room flows to the screen.
 * Who is talking comes from the room's LiveKit voice room, joined while
 * the lobby is open.
 */
class LobbyRealtimeClientImpl(
    private val realtime: RealtimeGateway,
    private val rooms: PlayRoomsApi,
    private val profile: ProfileApi,
    private val voice: VoiceClient,
) : LobbyRealtimeClient {

    override fun observeRoom(roomId: String): Flow<RoomSnapshot> = channelFlow {
        launch {
            realtime.stream(Channel.ROOM)
                .filter { it.type == MessageType.ROOM_SNAPSHOT }
                .map { realtime.decode(it, RoomSnapshot.serializer()) }
                .filter { it.room.id == roomId }
                .collect { send(it) }
        }
        val code = rooms.getMyRooms().rooms.firstOrNull { it.roomId == roomId }?.code ?: return@channelFlow
        realtime.connections.collect { count ->
            if (count > 0) {
                runCatching { realtime.request(Channel.ROOM, MessageType.JOIN_ROOM, JoinRoom.serializer(), JoinRoom(code)) }
                    .onFailure { CrashReporter.record(it, "LobbyRealtimeClient.enter") }
            }
        }
    }

    override fun observeMatchStarting(roomId: String): Flow<MatchStarting> =
        realtime.stream(Channel.ROOM)
            .filter { it.type == MessageType.MATCH_STARTING }
            .map { realtime.decode(it, MatchStarting.serializer()) }

    override fun observeSpeaking(roomId: String): Flow<Set<String>> = voice.speakers(VoiceTarget.Room(roomId))

    override suspend fun send(roomId: String, command: LobbyCommand) {
        when (command) {
            is LobbyCommand.SelectGame -> room(MessageType.SELECT_GAME, SelectGame.serializer(), SelectGame(command.gameId))
            is LobbyCommand.SetLeague -> room(MessageType.SET_LEAGUE, SetLeague.serializer(), SetLeague(command.entryCoins))
            is LobbyCommand.SetHouseRule -> room(MessageType.SET_HOUSE_RULE, SetHouseRule.serializer(), SetHouseRule(command.key, command.enabled))
            is LobbyCommand.SetBotFill -> room(MessageType.SET_BOT_FILL, SetBotFill.serializer(), SetBotFill(command.enabled))
            is LobbyCommand.SetReady -> room(MessageType.SET_READY, SetReady.serializer(), SetReady(command.ready))
            is LobbyCommand.SetWatching -> room(MessageType.SET_WATCHING, SetWatching.serializer(), SetWatching(command.watching))
            is LobbyCommand.SetVoiceState -> room(MessageType.SET_VOICE_STATE, SetVoiceState.serializer(), SetVoiceState(command.micOn, command.speakerOn))
            LobbyCommand.StartCountdown -> room(MessageType.START_COUNTDOWN, StartCountdown.serializer(), StartCountdown)
            LobbyCommand.CancelCountdown -> room(MessageType.CANCEL_COUNTDOWN, CancelCountdown.serializer(), CancelCountdown)
            is LobbyCommand.TransferLeader -> room(MessageType.TRANSFER_LEADER, TransferLeader.serializer(), TransferLeader(command.userId))
            is LobbyCommand.TransferAdmin -> room(MessageType.TRANSFER_ADMIN, TransferAdmin.serializer(), TransferAdmin(command.userId))
            is LobbyCommand.RemoveMember -> room(MessageType.REMOVE_MEMBER, RemoveMember.serializer(), RemoveMember(command.userId))
            is LobbyCommand.ApproveJoin -> room(MessageType.APPROVE_JOIN, ApproveJoin.serializer(), ApproveJoin(command.userId))
            is LobbyCommand.RenameRoom -> room(MessageType.RENAME_ROOM, RenameRoom.serializer(), RenameRoom(command.name))
            is LobbyCommand.DeclineJoin -> room(MessageType.DECLINE_JOIN, DeclineJoin.serializer(), DeclineJoin(command.userId))
            LobbyCommand.ExitLobby -> room(MessageType.EXIT_LOBBY, ExitLobby.serializer(), ExitLobby)
            LobbyCommand.LeaveRoom -> room(MessageType.LEAVE_ROOM, LeaveRoom.serializer(), LeaveRoom)
            // A member card's "Add Friend" sends a friend request to that person; "Remove" ends the friendship.
            is LobbyCommand.SetFriend ->
                if (command.friend) profile.sendFriendRequest(SendFriendRequestBody(userId = command.userId)) else profile.removeFriend(command.userId)
        }
    }

    private suspend fun <T> room(type: String, serializer: KSerializer<T>, value: T) {
        realtime.request(Channel.ROOM, type, serializer, value)
    }
}
