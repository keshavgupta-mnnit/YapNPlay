package com.kglabs28.yapnplay.appengine.games.doodleking.data.remote

import com.kglabs28.yapnplay.appengine.protocol.transport.MessageType
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import com.kglabs28.yapnplay.appengine.games.base.match.MatchKeepPlayingVote
import com.kglabs28.yapnplay.appengine.games.base.match.MatchEndVote
import com.kglabs28.yapnplay.appengine.features.seathandover.data.toDomain
import com.kglabs28.yapnplay.appengine.core.common.CrashReporter
import com.kglabs28.yapnplay.appengine.games.doodleking.data.remote.wire.DoodleDrawing
import com.kglabs28.yapnplay.appengine.games.doodleking.data.remote.wire.DoodleKingPublicState
import com.kglabs28.yapnplay.appengine.games.doodleking.data.remote.wire.DoodleKingWireEvent
import com.kglabs28.yapnplay.appengine.games.doodleking.data.remote.wire.DoodleClear
import com.kglabs28.yapnplay.appengine.games.doodleking.data.remote.wire.DoodleRelay
import com.kglabs28.yapnplay.appengine.games.doodleking.data.remote.wire.DoodleStroke
import com.kglabs28.yapnplay.appengine.games.doodleking.data.remote.wire.DoodleUndo
import com.kglabs28.yapnplay.appengine.games.doodleking.domain.DoodleEvent
import com.kglabs28.yapnplay.appengine.games.doodleking.domain.DoodleFeedKind
import com.kglabs28.yapnplay.appengine.games.doodleking.domain.DoodleFeedLine
import com.kglabs28.yapnplay.appengine.games.doodleking.domain.DoodleInkOp
import com.kglabs28.yapnplay.appengine.games.doodleking.domain.DoodleMatch
import com.kglabs28.yapnplay.appengine.games.doodleking.domain.DoodlePhase
import com.kglabs28.yapnplay.appengine.games.doodleking.domain.DoodlePlayer
import com.kglabs28.yapnplay.appengine.games.doodleking.domain.DoodleSpectator
import com.kglabs28.yapnplay.appengine.games.doodleking.domain.DoodleStrokeData
import com.kglabs28.yapnplay.appengine.games.doodleking.domain.DoodleUpdate
import com.kglabs28.yapnplay.appengine.protocol.MatchSeatStatus
import com.kglabs28.yapnplay.appengine.protocol.MatchSnapshot
import com.kglabs28.yapnplay.appengine.protocol.SeatPresence
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlin.time.Duration.Companion.milliseconds

/** `MatchSnapshot` (Doodle King's state and events) → [DoodleUpdate]; relayed drawing → [DoodleInkOp]. Server times move onto this device's clock. */
class DoodleKingSnapshotMapper(private val json: Json) {

    fun map(snapshot: MatchSnapshot, myUserId: String, now: Instant): DoodleUpdate {
        val state = json.decodeFromJsonElement(DoodleKingPublicState.serializer(), snapshot.publicState)
        val toLocal = { serverMillis: Long -> now + (serverMillis - snapshot.serverTime).milliseconds }
        val scores = state.seats.associateBy { it.seat }
        val match = DoodleMatch(
            matchId = snapshot.matchId,
            title = snapshot.title,
            roomId = snapshot.roomId,
            players = snapshot.seats.map { seat ->
                val score = scores[seat.seat]
                DoodlePlayer(
                    seat = seat.seat,
                    userId = seat.controllerUserId,
                    name = seat.displayName,
                    level = seat.level,
                    avatarId = seat.avatarId,
                    isMe = seat.seat == snapshot.viewer.seat,
                    micOn = seat.micOn,
                    isCpu = seat.cpu,
                    away = seat.presence != SeatPresence.PLAYING,
                    botHeld = seat.presence == SeatPresence.BOT,
                    removed = seat.status == MatchSeatStatus.REMOVED || score?.removed == true,
                    score = score?.score ?: 0,
                    guessed = score?.guessed == true,
                    turnPoints = score?.turnPoints ?: 0,
                )
            },
            spectators = snapshot.spectators.map { DoodleSpectator(it.userId, it.displayName, it.micOn, it.avatarId) },
            phase = DoodlePhase.entries.firstOrNull { it.name == state.phase } ?: DoodlePhase.INTRO,
            round = state.round,
            rounds = state.rounds,
            level = state.level,
            drawerSeat = state.drawerSeat,
            phaseEndsAt = toLocal(state.phaseEndsAt),
            drawSeconds = state.drawSeconds,
            word = state.word,
            options = state.options,
            hint = state.hint,
            wordCategory = state.wordCategory,
            wordLanguage = state.wordLanguage,
            feed = state.feed.map { DoodleFeedLine(DoodleFeedKind.entries.firstOrNull { k -> k.name == it.kind } ?: DoodleFeedKind.GUESS, it.seat, it.text, it.points) },
            drawingId = state.drawingId,
            strokeCount = state.strokeCount,
            startedAt = toLocal(snapshot.startedAt),
            viewerUserId = myUserId,
            seatOffer = snapshot.seatOffer?.toDomain(toLocal),
            canOfferSeats = snapshot.canOfferSeats,
            endVote = snapshot.endVote?.let { MatchEndVote(it.proposerSeat, toLocal(it.endsAt), it.voters, it.yes, it.no) },
            keepPlaying = snapshot.extensionVote?.let { MatchKeepPlayingVote(toLocal(it.endsAt), it.voters, it.yes, it.no, it.extraRounds, it.extraMinutes) },
        )
        val events = snapshot.events.filterNot { it[TYPE]?.jsonPrimitive?.contentOrNull in SHARED_EVENTS }.mapNotNull { event ->
            runCatching {
                when (val e = json.decodeFromJsonElement(DoodleKingWireEvent.serializer(), event)) {
                    is DoodleKingWireEvent.Guessed -> DoodleEvent.Guessed(e.seat, e.points)
                    is DoodleKingWireEvent.Revealed -> DoodleEvent.Revealed(e.word, e.points)
                }
            }.onFailure { CrashReporter.record(it, "DoodleKingSnapshotMapper.event") }.getOrNull()
        }
        return DoodleUpdate(match, events)
    }

    /** A forwarded drawing message; null for anything that isn't one (an emoji gift shares the relay). */
    fun ink(payload: JsonObject): DoodleInkOp? = if (payload[TYPE]?.jsonPrimitive?.contentOrNull !in DRAWING_TYPES) null else runCatching {
        when (val relay = json.decodeFromJsonElement(DoodleRelay.serializer(), payload)) {
            is DoodleStroke -> DoodleInkOp.Part(relay.drawingId, relay.toDomain())
            is DoodleUndo -> DoodleInkOp.Undo(relay.drawingId)
            is DoodleClear -> DoodleInkOp.Clear(relay.drawingId)
            is DoodleDrawing -> DoodleInkOp.Whole(relay.drawingId, relay.strokes.map { it.toDomain() })
        }
    }.onFailure { CrashReporter.record(it, "DoodleKingSnapshotMapper.ink") }.getOrNull()

    private fun DoodleStroke.toDomain() = DoodleStrokeData(id, colour, size, eraser, points)

    private companion object {
        const val TYPE = "type"
        /** The relay's drawing messages (the rest, like emoji gifts, belong to others). */
        val DRAWING_TYPES = setOf(MessageType.DOODLE_STROKE, MessageType.DOODLE_UNDO, MessageType.DOODLE_CLEAR, DRAWING)
        const val DRAWING = "DoodleDrawing"
    }

/** Every game's vote results (shown from the snapshot's own vote state), not Doodle King events. */
private val SHARED_EVENTS = setOf("ExtensionVoted", "EndVoted")
}
