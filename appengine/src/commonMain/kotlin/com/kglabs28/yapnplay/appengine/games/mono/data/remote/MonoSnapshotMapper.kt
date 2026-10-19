package com.kglabs28.yapnplay.appengine.games.mono.data.remote

import com.kglabs28.yapnplay.appengine.core.common.CrashReporter
import com.kglabs28.yapnplay.appengine.games.base.identity.SeatNumber
import com.kglabs28.yapnplay.appengine.games.mono.data.remote.wire.MonoCardWire
import com.kglabs28.yapnplay.appengine.games.mono.data.remote.wire.MonoPublicState
import com.kglabs28.yapnplay.appengine.games.mono.data.remote.wire.MonoWireEvent
import com.kglabs28.yapnplay.appengine.games.mono.domain.MonoCard
import com.kglabs28.yapnplay.appengine.games.mono.domain.MonoEvent
import com.kglabs28.yapnplay.appengine.games.mono.domain.MonoMatch
import com.kglabs28.yapnplay.appengine.games.mono.domain.MonoPlayer
import com.kglabs28.yapnplay.appengine.games.mono.domain.MonoRankedSeat
import com.kglabs28.yapnplay.appengine.games.mono.domain.MonoSeatOffer
import com.kglabs28.yapnplay.appengine.games.mono.domain.MonoSpectator
import com.kglabs28.yapnplay.appengine.games.mono.domain.MonoUpdate
import com.kglabs28.yapnplay.appengine.games.mono.identity.MonoConstants
import com.kglabs28.yapnplay.appengine.protocol.MatchSnapshot
import com.kglabs28.yapnplay.appengine.protocol.MatchSeatStatus
import com.kglabs28.yapnplay.appengine.protocol.SeatPresence
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json
import kotlin.time.Duration.Companion.milliseconds

/**
 * `MatchSnapshot` (Standard MONO public state and events) → [MonoUpdate].
 * Server times are moved onto this device's clock (now − serverTime).
 */
class MonoSnapshotMapper(private val json: Json) {

    fun map(snapshot: MatchSnapshot, myUserId: String, now: Instant): MonoUpdate {
        val state = json.decodeFromJsonElement(MonoPublicState.serializer(), snapshot.publicState)
        val toLocal = { serverMillis: Long -> now + (serverMillis - snapshot.serverTime).milliseconds }
        val hands = state.hands
        val match = MonoMatch(
            matchId = snapshot.matchId,
            title = snapshot.title,
            gameId = snapshot.gameId,
            players = snapshot.seats.map { seat ->
                val hand = hands[seat.seat].orEmpty().map { it.toDomain() }
                MonoPlayer(
                    seat = seat.seat,
                    userId = seat.controllerUserId,
                    name = seat.displayName,
                    level = seat.level,
                    isMe = seat.seat == snapshot.viewer.seat,
                    micOn = seat.micOn,
                    autoplay = seat.cpu || seat.autoPlay,
                    removed = seat.status == MatchSeatStatus.REMOVED,
                    isCpu = seat.cpu,
                    avatarId = seat.avatarId,
                    hand = hand,
                    cardCount = state.cardCounts[seat.seat] ?: hand.size,
                    presence = seat.presence.name,
                )
            },
            spectators = snapshot.spectators.map { MonoSpectator(it.userId, it.displayName, it.micOn) },
            spectatorCount = snapshot.spectators.size,
            currentSeat = snapshot.turn.seat,
            phase = state.phase,
            topCard = state.topCard?.toDomain(),
            topCardColor = state.topCardColor.toColor(),
            drawPileCount = state.drawPileCount,
            discardPileCount = state.discardPileCount,
            activeStackPenalty = state.activeStackPenalty,
            monoCallWindowExpiry = toLocal(state.monoCallWindowExpiry),
            eliminatedSeats = state.eliminatedSeats,
            finishedSeats = state.finishedSeats,
            turnEndsAt = snapshot.turn.deadline?.let(toLocal),
            startedAt = toLocal(snapshot.startedAt),
            ranking = state.ranking.mapIndexed { index, seat ->
                MonoRankedSeat(seat = seat, status = if (index == 0) FINISHED else IN_PLAY, place = index + 1)
            },
            seatOffer = snapshot.seatOffer?.let {
                MonoSeatOffer(offeredBy = it.seat, offeredTo = it.userId, expiresAt = toLocal(it.endsAt))
            },
            roomId = snapshot.roomId,
            viewerUserId = myUserId,
        )
        val events = snapshot.events.mapNotNull { event ->
            runCatching { json.decodeFromJsonElement(MonoWireEvent.serializer(), event).toDomain() }
                .onFailure { CrashReporter.record(it, "MonoSnapshotMapper.event") }
                .getOrNull()
        }
        return MonoUpdate(match, events)
    }

    private fun String?.toColor(): MonoConstants.Color? =
        this?.let { name -> MonoConstants.Color.entries.firstOrNull { it.name == name } }

    private fun MonoCardWire.toDomain(): MonoCard {
        val colour = color.toColor()
        return when (kind) {
            NUMBER -> MonoCard.NumberCard(colour ?: MonoConstants.Color.RED, number ?: 0)
            REVERSE -> MonoCard.ReverseCard(colour ?: MonoConstants.Color.RED)
            SKIP -> MonoCard.SkipCard(colour ?: MonoConstants.Color.RED)
            DRAW_TWO -> MonoCard.DrawTwoCard(colour ?: MonoConstants.Color.RED)
            DRAW_FOUR -> MonoCard.PrismDrawFour
            else -> MonoCard.PrismColorWild
        }
    }

    private fun MonoWireEvent.toDomain(): MonoEvent = when (this) {
        is MonoWireEvent.CardPlayed -> MonoEvent.CardPlayed(SeatNumber(seat), card.toDomain(), color.toColor())
        is MonoWireEvent.CardDrawn -> MonoEvent.CardDrawn(SeatNumber(seat), card.toDomain())
        is MonoWireEvent.ColorChosen -> MonoEvent.ColorChosen(SeatNumber(seat), color.toColor() ?: MonoConstants.Color.RED)
        is MonoWireEvent.MonoCalled -> MonoEvent.MonoCalled(SeatNumber(seat))
        is MonoWireEvent.MonoMissed -> MonoEvent.MonoMissed(SeatNumber(seat), penaltyCards)
        is MonoWireEvent.HandSwapped -> MonoEvent.HandSwapped(SeatNumber(from), SeatNumber(to))
        is MonoWireEvent.HandsPassed -> MonoEvent.HandsPassed(direction)
        is MonoWireEvent.TurnPassed -> MonoEvent.TurnPassed(SeatNumber(seat))
        is MonoWireEvent.SeatFinished -> MonoEvent.SeatFinished(SeatNumber(seat), place)
        is MonoWireEvent.MatchEnded -> MonoEvent.MatchEnded(finishingOrder)
    }

    private companion object {
        const val FINISHED = "FINISHED"
        const val IN_PLAY = "IN_PLAY"
        const val NUMBER = "NumberCard"
        const val REVERSE = "ReverseCard"
        const val SKIP = "SkipCard"
        const val DRAW_TWO = "DrawTwoCard"
        const val DRAW_FOUR = "PrismDrawFour"
    }
}