package com.kglabs28.yapnplay.appengine.games.royalludo.data.remote

import com.kglabs28.yapnplay.appengine.core.common.CrashReporter
import com.kglabs28.yapnplay.appengine.features.seathandover.data.toDomain
import com.kglabs28.yapnplay.appengine.games.royalludo.data.remote.wire.RoyalLudoPublicState
import com.kglabs28.yapnplay.appengine.games.royalludo.data.remote.wire.RoyalLudoWireEvent
import com.kglabs28.yapnplay.appengine.games.royalludo.domain.LudoColour
import com.kglabs28.yapnplay.appengine.games.royalludo.domain.LudoEvent
import com.kglabs28.yapnplay.appengine.games.royalludo.domain.LudoMatch
import com.kglabs28.yapnplay.appengine.games.royalludo.domain.LudoPhase
import com.kglabs28.yapnplay.appengine.games.royalludo.domain.LudoPlayer
import com.kglabs28.yapnplay.appengine.games.royalludo.domain.LudoPresence
import com.kglabs28.yapnplay.appengine.games.royalludo.domain.LudoSpectator
import com.kglabs28.yapnplay.appengine.games.royalludo.domain.LudoUpdate
import com.kglabs28.yapnplay.appengine.protocol.MatchSeatStatus
import com.kglabs28.yapnplay.appengine.protocol.MatchSnapshot
import com.kglabs28.yapnplay.appengine.protocol.SeatPresence
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json
import kotlin.time.Duration.Companion.milliseconds

/**
 * `MatchSnapshot` (Royal Ludo public state and events) → [LudoUpdate].
 * Server times are moved onto this device's clock (now − serverTime).
 */
class LudoSnapshotMapper(private val json: Json) {

    fun map(snapshot: MatchSnapshot, myUserId: String, now: Instant): LudoUpdate {
        val state = json.decodeFromJsonElement(RoyalLudoPublicState.serializer(), snapshot.publicState)
        val toLocal = { serverMillis: Long -> now + (serverMillis - snapshot.serverTime).milliseconds }
        val phase = when (state.phase) {
            PHASE_MOVE -> LudoPhase.MOVE
            PHASE_FINISHED -> LudoPhase.FINISHED
            else -> LudoPhase.ROLL
        }
        val match = LudoMatch(
            matchId = snapshot.matchId,
            title = snapshot.title,
            players = snapshot.seats.map { seat ->
                LudoPlayer(
                    seat = seat.seat,
                    userId = seat.controllerUserId,
                    name = seat.displayName,
                    level = seat.level,
                    colour = LudoColour.entries.firstOrNull { it.name == seat.colour.name } ?: LudoColour.RED,
                    isMe = seat.seat == snapshot.viewer.seat,
                    micOn = seat.micOn,
                    autoplay = seat.cpu || seat.autoPlay,
                    removed = seat.status == MatchSeatStatus.REMOVED,
                    isCpu = seat.cpu,
                    avatarId = seat.avatarId,
                    presence = when (seat.presence) {
                        SeatPresence.PLAYING -> LudoPresence.PLAYING
                        SeatPresence.AWAY -> LudoPresence.AWAY
                        SeatPresence.BOT -> LudoPresence.BOT
                    },
                )
            },
            spectators = snapshot.spectators.map { LudoSpectator(it.userId, it.displayName, it.micOn, it.avatarId) },
            spectatorCount = snapshot.spectators.size,
            pieces = state.pieces,
            currentSeat = snapshot.turn.seat,
            phase = phase,
            lastRoll = state.lastRoll,
            legalPieces = state.legalPieces,
            turnEndsAt = snapshot.turn.deadline?.let(toLocal),
            startedAt = toLocal(snapshot.startedAt),
            finishedSeats = state.finishedSeats,
            ranking = state.ranking,
            pot = snapshot.pot?.total ?: 0,
            seatOffer = snapshot.seatOffer?.toDomain(toLocal),
            roomId = snapshot.roomId,
            viewerUserId = myUserId,
        )
        val events = snapshot.events.mapNotNull { event ->
            runCatching { json.decodeFromJsonElement(RoyalLudoWireEvent.serializer(), event).toDomain() }
                .onFailure { CrashReporter.record(it, "LudoSnapshotMapper.event") }
                .getOrNull()
        }
        return LudoUpdate(match, events)
    }

    private fun RoyalLudoWireEvent.toDomain(): LudoEvent = when (this) {
        is RoyalLudoWireEvent.Rolled -> LudoEvent.Rolled(seat, value)
        is RoyalLudoWireEvent.Moved -> LudoEvent.Moved(seat, piece, from, to)
        is RoyalLudoWireEvent.Captured -> LudoEvent.Captured(seat, victimSeat, victimPiece)
        is RoyalLudoWireEvent.TurnPassed -> LudoEvent.TurnPassed(seat)
        is RoyalLudoWireEvent.Reverted -> LudoEvent.Reverted(seat, pieces)
    }

    private companion object {
        const val PHASE_MOVE = "MOVE"
        const val PHASE_FINISHED = "FINISHED"
    }
}
