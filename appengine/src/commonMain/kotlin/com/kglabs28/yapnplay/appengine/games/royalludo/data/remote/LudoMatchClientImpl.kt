package com.kglabs28.yapnplay.appengine.games.royalludo.data.remote

import com.kglabs28.yapnplay.appengine.games.royalludo.domain.LudoCommand
import com.kglabs28.yapnplay.appengine.games.royalludo.domain.LudoUpdate
import kotlinx.coroutines.flow.Flow

/**
 * Match client. TODAY: every call goes to [FakeLudoMatchServer], which
 * runs the real engine on the device.
 *
 * LATER (backend ready): uncomment the production paths (WebSocket
 * connection manager, report Phase 12; voice client, Phase 13), map
 * `MatchSnapshot` + `MatchEvent` to [LudoUpdate] with the game's public
 * state, and delete the fake lines. Nothing above changes.
 */
class LudoMatchClientImpl(
    private val fakeServer: FakeLudoMatchServer,
) : LudoMatchClient {

    override fun observeMatch(matchId: String): Flow<LudoUpdate> {
        // --- production path ---
        // return realtime.stream(Channel.MATCH)
        //     .onStart { realtime.send(Channel.MATCH, MessageType.MATCH_REQUEST_SNAPSHOT, MatchRequestSnapshot(matchId, lastVersion = 0)) }
        //     .filter { it.type == MessageType.MATCH_SNAPSHOT }
        //     .map { it.decode<MatchSnapshot>().toLudoUpdate(myUserId) }

        // --- fake path ---
        return fakeServer.match(matchId)
    }

    override fun observeSpeaking(matchId: String): Flow<Set<String>> {
        // --- production path ---
        // return voiceClient.activeSpeakers

        // --- fake path ---
        return fakeServer.speaking()
    }

    override suspend fun send(matchId: String, command: LudoCommand) {
        // --- production path: one match-channel message per command ---
        // when (command) {
        //     LudoCommand.Roll -> realtime.request(Channel.MATCH, MessageType.ROLL_DICE, RollDiceRequest(matchId))
        //     is LudoCommand.Move -> realtime.request(Channel.MATCH, MessageType.MOVE_PIECE, MovePieceRequest(matchId, command.piece.toString()))
        //     is LudoCommand.SetAutoplay -> realtime.request(Channel.MATCH, MessageType.SET_AUTOPLAY, SetAutoplay(matchId, command.enabled))
        //     is LudoCommand.SetMic -> voiceClient.setMicEnabled(command.on)
        //     LudoCommand.Leave -> realtime.request(Channel.MATCH, MessageType.LEAVE_MATCH, LeaveMatch(matchId))
        // }

        // --- fake path ---
        fakeServer.handle(command)
    }
}
