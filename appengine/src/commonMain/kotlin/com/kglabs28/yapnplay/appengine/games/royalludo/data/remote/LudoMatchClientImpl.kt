package com.kglabs28.yapnplay.appengine.games.royalludo.data.remote

import com.kglabs28.yapnplay.appengine.core.common.AppClock
import com.kglabs28.yapnplay.appengine.core.network.BackendSession
import com.kglabs28.yapnplay.appengine.core.network.MatchChannel
import com.kglabs28.yapnplay.appengine.features.voice.data.remote.VoiceClient
import com.kglabs28.yapnplay.appengine.features.voice.data.remote.VoiceTarget
import com.kglabs28.yapnplay.appengine.games.royalludo.domain.LudoCommand
import com.kglabs28.yapnplay.appengine.games.royalludo.domain.LudoUpdate
import com.kglabs28.yapnplay.appengine.protocol.transport.MessageType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

/**
 * Royal Ludo over the MATCH channel: snapshots in, one message per
 * command out. Who is talking comes from the
 * table's LiveKit voice room, joined while the match is open.
 */
class LudoMatchClientImpl(
    private val match: MatchChannel,
    private val mapper: LudoSnapshotMapper,
    private val session: BackendSession,
    private val clock: AppClock,
    private val voice: VoiceClient,
) : LudoMatchClient {

    override fun observeMatch(matchId: String): Flow<LudoUpdate> =
        match.snapshots(matchId).map { mapper.map(it, session.currentUserId().orEmpty(), clock.now()) }

    override fun observeSpeaking(matchId: String): Flow<Set<String>> = voice.speakers(VoiceTarget.Match(matchId))

    override suspend fun send(matchId: String, command: LudoCommand) {
        when (command) {
            LudoCommand.Roll -> match.request(MessageType.ROLL_DICE, JsonObject.serializer(), buildJsonObject { put(MATCH_ID, matchId) })
            is LudoCommand.Move -> match.request(
                MessageType.MOVE_PIECE,
                JsonObject.serializer(),
                buildJsonObject {
                    put(MATCH_ID, matchId)
                    put(PIECE_ID, command.piece.toString())
                },
            )
            is LudoCommand.SetAutoplay -> match.setAutoplay(matchId, command.enabled)
            is LudoCommand.SetMic -> match.setMic(matchId, command.on)
            is LudoCommand.Leave -> match.leave(matchId, command.giveToUserId)
            LudoCommand.StillHere -> match.stillHere(matchId)
            is LudoCommand.AnswerSeatOffer -> match.answerSeatOffer(matchId, command.accept)
            is LudoCommand.ChooseSeatTaker -> match.chooseSeatTaker(matchId, command.giveToUserId)
            is LudoCommand.SetAway -> match.setAway(matchId, command.away)
            is LudoCommand.TakeSeat -> match.takeSeat(matchId, command.seat)
        }
    }

    private companion object {
        const val MATCH_ID = "matchId"
        const val PIECE_ID = "pieceId"
    }
}
