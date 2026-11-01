package com.kglabs28.yapnplay.appengine.games.doodleking.data.remote

import com.kglabs28.yapnplay.appengine.core.common.AppClock
import com.kglabs28.yapnplay.appengine.core.network.MatchChannel
import com.kglabs28.yapnplay.appengine.core.network.SessionProvider
import com.kglabs28.yapnplay.appengine.features.voice.data.remote.VoiceClient
import com.kglabs28.yapnplay.appengine.features.voice.data.remote.VoiceTarget
import com.kglabs28.yapnplay.appengine.games.doodleking.data.remote.wire.DoodleKingWireAction
import com.kglabs28.yapnplay.appengine.games.doodleking.data.remote.wire.DoodleStrokeOut
import com.kglabs28.yapnplay.appengine.games.doodleking.data.remote.wire.DoodleSyncOut
import com.kglabs28.yapnplay.appengine.games.doodleking.data.remote.wire.DoodleTurnOut
import com.kglabs28.yapnplay.appengine.games.doodleking.domain.DoodleCommand
import com.kglabs28.yapnplay.appengine.games.doodleking.domain.DoodleInkOp
import com.kglabs28.yapnplay.appengine.games.doodleking.domain.DoodleUpdate
import com.kglabs28.yapnplay.appengine.protocol.transport.MessageType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject

/**
 * Doodle King over the MATCH channel: snapshots in, moves out as `GameAction`.
 * The drawing has its own light messages: the drawer's strokes go out as
 * they're drawn and come back to everyone else as `MatchRelay`.
 */
class DoodleKingMatchClient(
    private val match: MatchChannel,
    private val mapper: DoodleKingSnapshotMapper,
    private val session: SessionProvider,
    private val clock: AppClock,
    private val voice: VoiceClient,
    private val json: Json,
) {

    fun observeMatch(matchId: String): Flow<DoodleUpdate> =
        match.snapshots(matchId).map { mapper.map(it, session.currentUserId().orEmpty(), clock.now()) }

    /** The drawer's strokes, undo and clear, and the whole drawing when asked for. */
    fun observeInk(): Flow<DoodleInkOp> = match.relays().mapNotNull(mapper::ink)

    fun observeSpeaking(matchId: String): Flow<Set<String>> = voice.speakers(VoiceTarget.Match(matchId))

    suspend fun send(matchId: String, command: DoodleCommand) {
        when (command) {
            is DoodleCommand.PickWord -> play(matchId, DoodleKingWireAction.PickWord(command.index))
            is DoodleCommand.Guess -> play(matchId, DoodleKingWireAction.Guess(command.text))
            DoodleCommand.Pass -> play(matchId, DoodleKingWireAction.Pass)
            is DoodleCommand.SetMic -> match.setMic(matchId, command.on)
            is DoodleCommand.Leave -> match.leave(matchId, giveToUserId = command.giveToUserId)
            DoodleCommand.ProposeEnd -> match.proposeEnd(matchId)
            is DoodleCommand.VoteEnd -> match.castEndVote(matchId, command.agree)
            is DoodleCommand.VoteKeepPlaying -> match.voteExtension(matchId, command.keepPlaying)
        }
    }

    /** One drawing change from the user (the drawer). */
    suspend fun sendInk(matchId: String, op: DoodleInkOp) {
        when (op) {
            is DoodleInkOp.Part -> op.stroke.let {
                match.request(MessageType.DOODLE_STROKE, DoodleStrokeOut.serializer(), DoodleStrokeOut(matchId, op.drawingId, it.id, it.colour, it.size, it.eraser, it.points))
            }
            is DoodleInkOp.Undo -> match.request(MessageType.DOODLE_UNDO, DoodleTurnOut.serializer(), DoodleTurnOut(matchId, op.drawingId))
            is DoodleInkOp.Clear -> match.request(MessageType.DOODLE_CLEAR, DoodleTurnOut.serializer(), DoodleTurnOut(matchId, op.drawingId))
            is DoodleInkOp.Whole -> Unit
        }
    }

    /** Asks for the whole drawing (joined late, or strokes went missing). */
    suspend fun requestDrawing(matchId: String) = match.request(MessageType.DOODLE_SYNC, DoodleSyncOut.serializer(), DoodleSyncOut(matchId))

    private suspend fun play(matchId: String, action: DoodleKingWireAction) =
        match.gameAction(matchId, json.encodeToJsonElement(DoodleKingWireAction.serializer(), action).jsonObject)
}
