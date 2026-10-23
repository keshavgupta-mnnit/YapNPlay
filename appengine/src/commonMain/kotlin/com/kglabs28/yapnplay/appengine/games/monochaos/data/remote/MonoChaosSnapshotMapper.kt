package com.kglabs28.yapnplay.appengine.games.monochaos.data.remote

import com.kglabs28.yapnplay.appengine.core.common.CrashReporter
import com.kglabs28.yapnplay.appengine.games.mono.domain.MonoContinueVote
import com.kglabs28.yapnplay.appengine.games.base.identity.SeatNumber
import com.kglabs28.yapnplay.appengine.games.monochaos.data.remote.wire.MonoChaosCardWire
import com.kglabs28.yapnplay.appengine.games.monochaos.data.remote.wire.MonoChaosPublicState
import com.kglabs28.yapnplay.appengine.games.monochaos.data.remote.wire.MonoChaosWireEvent
import com.kglabs28.yapnplay.appengine.games.monochaos.domain.MonoChaosCard
import com.kglabs28.yapnplay.appengine.games.monochaos.domain.MonoChaosEvent
import com.kglabs28.yapnplay.appengine.games.monochaos.domain.MonoChaosMatch
import com.kglabs28.yapnplay.appengine.games.monochaos.domain.MonoChaosPlayer
import com.kglabs28.yapnplay.appengine.games.monochaos.domain.MonoChaosRankedSeat
import com.kglabs28.yapnplay.appengine.games.monochaos.domain.MonoChaosSeatOffer
import com.kglabs28.yapnplay.appengine.games.monochaos.domain.MonoChaosSpectator
import com.kglabs28.yapnplay.appengine.games.monochaos.domain.MonoChaosUpdate
import com.kglabs28.yapnplay.appengine.games.monochaos.identity.MonoChaosConstants
import com.kglabs28.yapnplay.appengine.protocol.MatchSnapshot
import com.kglabs28.yapnplay.appengine.protocol.MatchSeatStatus
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json
import kotlin.time.Duration.Companion.milliseconds

/**
 * `MatchSnapshot` (MONO: Complete Chaos public state and events) →
 * [MonoChaosUpdate]. Server times are moved onto this device's clock.
 */
class MonoChaosSnapshotMapper(private val json: Json) {

    fun map(snapshot: MatchSnapshot, myUserId: String, now: Instant): MonoChaosUpdate {
        val state = json.decodeFromJsonElement(MonoChaosPublicState.serializer(), snapshot.publicState)
        val toLocal = { serverMillis: Long -> now + (serverMillis - snapshot.serverTime).milliseconds }
        val hands = state.hands
        val match = MonoChaosMatch(
            matchId = snapshot.matchId,
            title = snapshot.title,
            gameId = snapshot.gameId,
            players = snapshot.seats.map { seat ->
                val hand = hands[seat.seat].orEmpty().map { it.toDomain() }
                MonoChaosPlayer(
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
            spectators = snapshot.spectators.map { MonoChaosSpectator(it.userId, it.displayName, it.micOn) },
            spectatorCount = snapshot.spectators.size,
            currentSeat = snapshot.turn.seat,
            phase = state.phase,
            topCard = state.topCard?.toDomain(),
            topCardColor = state.topCardColor.toColor(),
            drawPileCount = state.drawPileCount,
            discardPileCount = state.discardPileCount,
            activeStackPenalty = state.activeStackPenalty,
            monoCallWindowExpiry = toLocal(state.monoCallWindowExpiry),
            monoWindowSeat = state.monoWindowSeat,
            monoCalledSeat = state.monoCalledSeat,
            stackTopValue = state.stackTopValue,
            reversed = state.reversed,
            drawnCardIndex = state.drawnCardIndex,
            pendingSwapSeat = state.pendingSwapSeat,
            eliminatedSeats = state.eliminatedSeats,
            finishedSeats = state.finishedSeats,
            turnEndsAt = snapshot.turn.deadline?.let(toLocal),
            startedAt = toLocal(snapshot.startedAt),
            ranking = state.ranking.mapIndexed { index, seat ->
                MonoChaosRankedSeat(seat = seat, status = if (index == 0) FINISHED else IN_PLAY, place = index + 1)
            },
            seatOffer = snapshot.seatOffer?.let {
                MonoChaosSeatOffer(offeredBy = it.seat, offeredTo = it.userId, expiresAt = toLocal(it.endsAt))
            },
            catchSeat = state.catchSeat,
            catchClosedFor = state.catchClosedFor,
            continueVote = state.continueVote?.let { MonoContinueVote(toLocal(it.endsAt), it.voters, it.yes, it.no) },
            caughtPenaltyCards = state.caughtPenaltyCards,
            falseCatchPenaltyCards = state.falseCatchPenaltyCards,
            roomId = snapshot.roomId,
            viewerUserId = myUserId,
        )
        val events = snapshot.events.mapNotNull { event ->
            runCatching { json.decodeFromJsonElement(MonoChaosWireEvent.serializer(), event).toDomain() }
                .onFailure { CrashReporter.record(it, "MonoChaosSnapshotMapper.event") }
                .getOrNull()
        }
        return MonoChaosUpdate(match, events)
    }

    private fun String?.toColor(): MonoChaosConstants.Color? =
        this?.let { name -> MonoChaosConstants.Color.entries.firstOrNull { it.name == name } }

    private fun MonoChaosCardWire.toDomain(): MonoChaosCard {
        val colour = color.toColor() ?: MonoChaosConstants.Color.RED
        return when (kind) {
            NUMBER -> MonoChaosCard.NumberCard(colour, number ?: 0)
            REVERSE -> MonoChaosCard.ReverseCard(colour)
            SKIP -> MonoChaosCard.SkipCard(colour)
            DRAW_TWO -> MonoChaosCard.DrawTwoCard(colour)
            COLORED_DRAW_FOUR -> MonoChaosCard.ColoredDrawFourCard(colour)
            DISCARD_ALL -> MonoChaosCard.DiscardAllCard(colour)
            SKIP_EVERYONE -> MonoChaosCard.SkipEveryoneCard(colour)
            DRAW_FOUR -> MonoChaosCard.PrismDrawFour
            REVERSE_DRAW_FOUR -> MonoChaosCard.PrismReverseDrawFour
            DRAW_SIX -> MonoChaosCard.PrismDrawSix
            DRAW_TEN -> MonoChaosCard.PrismDrawTen
            ROULETTE -> MonoChaosCard.PrismColorRoulette
            else -> MonoChaosCard.PrismColorWild
        }
    }

    private fun MonoChaosWireEvent.toDomain(): MonoChaosEvent = when (this) {
        is MonoChaosWireEvent.CardPlayed -> MonoChaosEvent.CardPlayed(SeatNumber(seat), card.toDomain(), color.toColor())
        is MonoChaosWireEvent.CardDrawn -> MonoChaosEvent.CardDrawn(SeatNumber(seat), card.toDomain())
        is MonoChaosWireEvent.CardsDrawn -> MonoChaosEvent.CardsDrawn(SeatNumber(seat), count)
        is MonoChaosWireEvent.CardsDiscarded -> MonoChaosEvent.CardsDiscarded(SeatNumber(seat), count)
        is MonoChaosWireEvent.ColorChosen -> MonoChaosEvent.ColorChosen(SeatNumber(seat), color.toColor() ?: FALLBACK_COLOUR)
        is MonoChaosWireEvent.RouletteColorChosen -> MonoChaosEvent.RouletteColorChosen(SeatNumber(victim), color.toColor() ?: FALLBACK_COLOUR)
        is MonoChaosWireEvent.StackDraw -> MonoChaosEvent.StackDraw(SeatNumber(seat), card.toDomain(), newStackTotal)
        is MonoChaosWireEvent.StackAccepted -> MonoChaosEvent.StackAccepted(SeatNumber(seat), penaltyCards)
        is MonoChaosWireEvent.MonoCalled -> MonoChaosEvent.MonoCalled(SeatNumber(seat))
        is MonoChaosWireEvent.MonoMissed -> MonoChaosEvent.MonoMissed(SeatNumber(seat), penaltyCards)
        is MonoChaosWireEvent.MonoCaught -> MonoChaosEvent.MonoCaught(SeatNumber(catcher), SeatNumber(seat), penaltyCards)
        is MonoChaosWireEvent.FalseCatch -> MonoChaosEvent.FalseCatch(SeatNumber(seat), SeatNumber(target), penaltyCards)
        is MonoChaosWireEvent.ContinueVoted -> MonoChaosEvent.ContinueVoted(continued, yes, voters)
        is MonoChaosWireEvent.DeckReshuffled -> MonoChaosEvent.DeckReshuffled(drawPileCount)
        is MonoChaosWireEvent.HandSwapped -> MonoChaosEvent.HandSwapped(SeatNumber(from), SeatNumber(to))
        is MonoChaosWireEvent.HandsPassed -> MonoChaosEvent.HandsPassed(direction)
        is MonoChaosWireEvent.PlayerEliminated -> MonoChaosEvent.PlayerEliminated(SeatNumber(seat))
        is MonoChaosWireEvent.TurnPassed -> MonoChaosEvent.TurnPassed(SeatNumber(seat))
        is MonoChaosWireEvent.SeatFinished -> MonoChaosEvent.SeatFinished(SeatNumber(seat), place)
        is MonoChaosWireEvent.MatchEnded -> MonoChaosEvent.MatchEnded(finishingOrder)
    }

    private companion object {
        val FALLBACK_COLOUR = MonoChaosConstants.Color.RED
        const val FINISHED = "FINISHED"
        const val IN_PLAY = "IN_PLAY"
        const val NUMBER = "NumberCard"
        const val REVERSE = "ReverseCard"
        const val SKIP = "SkipCard"
        const val DRAW_TWO = "DrawTwoCard"
        const val COLORED_DRAW_FOUR = "ColoredDrawFourCard"
        const val DISCARD_ALL = "DiscardAllCard"
        const val SKIP_EVERYONE = "SkipEveryoneCard"
        const val DRAW_FOUR = "PrismDrawFour"
        const val REVERSE_DRAW_FOUR = "PrismReverseDrawFour"
        const val DRAW_SIX = "PrismDrawSix"
        const val DRAW_TEN = "PrismDrawTen"
        const val ROULETTE = "PrismColorRoulette"
    }
}