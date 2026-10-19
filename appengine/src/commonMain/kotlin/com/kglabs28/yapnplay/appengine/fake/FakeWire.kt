package com.kglabs28.yapnplay.appengine.fake

import com.kglabs28.yapnplay.appengine.games.base.match.GameState
import com.kglabs28.yapnplay.appengine.games.mono.data.remote.wire.MonoCardWire
import com.kglabs28.yapnplay.appengine.games.mono.data.remote.wire.MonoPublicState
import com.kglabs28.yapnplay.appengine.games.mono.data.remote.wire.MonoWireEvent
import com.kglabs28.yapnplay.appengine.games.mono.domain.MonoCard
import com.kglabs28.yapnplay.appengine.games.mono.domain.MonoEvent
import com.kglabs28.yapnplay.appengine.games.mono.domain.MonoState
import com.kglabs28.yapnplay.appengine.games.monochaos.data.remote.wire.MonoChaosCardWire
import com.kglabs28.yapnplay.appengine.games.monochaos.data.remote.wire.MonoChaosPublicState
import com.kglabs28.yapnplay.appengine.games.monochaos.data.remote.wire.MonoChaosWireEvent
import com.kglabs28.yapnplay.appengine.games.monochaos.domain.MonoChaosCard
import com.kglabs28.yapnplay.appengine.games.monochaos.domain.MonoChaosEvent
import com.kglabs28.yapnplay.appengine.games.monochaos.domain.MonoChaosState
import com.kglabs28.yapnplay.appengine.games.monochaos.identity.MonoChaosConstants

/**
 * Turns a live engine state into the same wire shapes the real backend
 * would send, so the app's own snapshot mappers decode it unchanged.
 * This is what lets the local host fake the server honestly: it uses
 * the real engine, and only the transport is fake.
 */

// ── Standard MONO ──────────────────────────────────────────────────────────

/** A Standard MONO card as the wire carries it. */
fun MonoCard.toWire(): MonoCardWire = when (this) {
    is MonoCard.NumberCard -> MonoCardWire(NUMBER, color.name, number)
    is MonoCard.ReverseCard -> MonoCardWire(REVERSE, color.name)
    is MonoCard.SkipCard -> MonoCardWire(SKIP, color.name)
    is MonoCard.DrawTwoCard -> MonoCardWire(DRAW_TWO, color.name)
    MonoCard.PrismColorWild -> MonoCardWire(PRISM)
    MonoCard.PrismDrawFour -> MonoCardWire(DRAW_FOUR)
}

/** The public state for [viewerSeat] (null: a spectator, who sees no hands). */
fun MonoState.toPublicState(viewerSeat: Int?): MonoPublicState {
    val seats = seats.map { it.value }
    return MonoPublicState(
        phase = phase.name,
        topCard = discardPile.lastOrNull()?.toWire(),
        topCardColor = topCardColor?.name,
        // Only the viewer ever receives their own cards.
        hands = viewerSeat?.let { mapOf(it to handForSeat(it).map(MonoCard::toWire)) } ?: emptyMap(),
        cardCounts = seats.associateWith { cardCountForSeat(it) },
        drawPileCount = drawPile.size,
        discardPileCount = discardPile.size,
        activeStackPenalty = activeStackPenalty,
        monoCallWindowExpiry = monoCallWindowExpiry,
        eliminatedSeats = eliminatedSeats.toList(),
        finishedSeats = finishedSeats(),
        ranking = ranking(),
    )
}

/** Seats that emptied their hand, first one first. */
private fun MonoState.finishedSeats(): List<Int> = seats.map { it.value }.filter { cardCountForSeat(it) == 0 }

/** Seats from fewest cards to most, once the match is over. */
private fun MonoState.ranking(): List<Int> =
    seats.map { it.value }.sortedBy { cardCountForSeat(it) }

/** A Standard MONO event as the wire carries it. */
fun MonoEvent.toWire(): MonoWireEvent = when (this) {
    is MonoEvent.CardPlayed -> MonoWireEvent.CardPlayed(seat.value, card.toWire(), color?.name)
    is MonoEvent.CardDrawn -> MonoWireEvent.CardDrawn(seat.value, card.toWire())
    is MonoEvent.ColorChosen -> MonoWireEvent.ColorChosen(seat.value, color.name)
    is MonoEvent.MonoCalled -> MonoWireEvent.MonoCalled(seat.value)
    is MonoEvent.MonoMissed -> MonoWireEvent.MonoMissed(seat.value, penaltyCards)
    is MonoEvent.HandSwapped -> MonoWireEvent.HandSwapped(from.value, to.value)
    is MonoEvent.HandsPassed -> MonoWireEvent.HandsPassed(direction)
    is MonoEvent.TurnPassed -> MonoWireEvent.TurnPassed(seat.value)
    is MonoEvent.SeatFinished -> MonoWireEvent.SeatFinished(seat.value, place)
    is MonoEvent.MatchEnded -> MonoWireEvent.MatchEnded(finishingOrder)
}

// ── MONO: Complete Chaos ────────────────────────────────────────────────────

/** A MONO: Complete Chaos card as the wire carries it. */
fun MonoChaosCard.toWire(): MonoChaosCardWire = when (this) {
    is MonoChaosCard.NumberCard -> MonoChaosCardWire(NUMBER, color.name, number)
    is MonoChaosCard.ReverseCard -> MonoChaosCardWire(REVERSE, color.name)
    is MonoChaosCard.SkipCard -> MonoChaosCardWire(SKIP, color.name)
    is MonoChaosCard.DrawTwoCard -> MonoChaosCardWire(DRAW_TWO, color.name)
    is MonoChaosCard.ColoredDrawFourCard -> MonoChaosCardWire(COLORED_DRAW_FOUR, color.name)
    is MonoChaosCard.DiscardAllCard -> MonoChaosCardWire(DISCARD_ALL, color.name)
    is MonoChaosCard.SkipEveryoneCard -> MonoChaosCardWire(SKIP_EVERYONE, color.name)
    MonoChaosCard.PrismColorWild -> MonoChaosCardWire(PRISM)
    MonoChaosCard.PrismDrawFour -> MonoChaosCardWire(DRAW_FOUR)
    MonoChaosCard.PrismReverseDrawFour -> MonoChaosCardWire(REVERSE_DRAW_FOUR)
    MonoChaosCard.PrismDrawSix -> MonoChaosCardWire(DRAW_SIX)
    MonoChaosCard.PrismDrawTen -> MonoChaosCardWire(DRAW_TEN)
    MonoChaosCard.PrismColorRoulette -> MonoChaosCardWire(ROULETTE)
}

/** The public state for [viewerSeat] (null: a spectator, who sees no hands). */
fun MonoChaosState.toPublicState(viewerSeat: Int?): MonoChaosPublicState {
    val seats = seats.map { it.value }
    return MonoChaosPublicState(
        phase = phase.name,
        topCard = discardPile.lastOrNull()?.toWire(),
        topCardColor = topCardColor?.name,
        hands = viewerSeat?.let { mapOf(it to handForSeat(it).map(MonoChaosCard::toWire)) } ?: emptyMap(),
        cardCounts = seats.associateWith { cardCountForSeat(it) },
        drawPileCount = drawPile.size,
        discardPileCount = discardPile.size,
        activeStackPenalty = activeStackPenalty,
        monoCallWindowExpiry = monoCallWindowExpiry,
        eliminatedSeats = eliminatedSeats.toList(),
        finishedSeats = seats.filter { cardCountForSeat(it) == 0 },
        ranking = seats.sortedBy { cardCountForSeat(it) },
    )
}

/** A MONO: Complete Chaos event as the wire carries it. */
fun MonoChaosEvent.toWire(): MonoChaosWireEvent = when (this) {
    is MonoChaosEvent.CardPlayed -> MonoChaosWireEvent.CardPlayed(seat.value, card.toWire(), color?.name)
    is MonoChaosEvent.CardDrawn -> MonoChaosWireEvent.CardDrawn(seat.value, card.toWire())
    is MonoChaosEvent.CardsDrawn -> MonoChaosWireEvent.CardsDrawn(seat.value, count)
    is MonoChaosEvent.ColorChosen -> MonoChaosWireEvent.ColorChosen(seat.value, color.name)
    is MonoChaosEvent.RouletteColorChosen -> MonoChaosWireEvent.RouletteColorChosen(victim.value, color.name)
    is MonoChaosEvent.StackDraw -> MonoChaosWireEvent.StackDraw(seat.value, card.toWire(), newStackTotal)
    is MonoChaosEvent.StackAccepted -> MonoChaosWireEvent.StackAccepted(seat.value, penaltyCards)
    is MonoChaosEvent.MonoCalled -> MonoChaosWireEvent.MonoCalled(seat.value)
    is MonoChaosEvent.MonoMissed -> MonoChaosWireEvent.MonoMissed(seat.value, penaltyCards)
    is MonoChaosEvent.HandSwapped -> MonoChaosWireEvent.HandSwapped(from.value, to.value)
    is MonoChaosEvent.HandsPassed -> MonoChaosWireEvent.HandsPassed(direction)
    is MonoChaosEvent.PlayerEliminated -> MonoChaosWireEvent.PlayerEliminated(seat.value)
    is MonoChaosEvent.TurnPassed -> MonoChaosWireEvent.TurnPassed(seat.value)
    is MonoChaosEvent.SeatFinished -> MonoChaosWireEvent.SeatFinished(seat.value, place)
    is MonoChaosEvent.MatchEnded -> MonoChaosWireEvent.MatchEnded(finishingOrder)
}

// ── Shared helpers ─────────────────────────────────────────────────────────

/** The seat the engine is waiting on. */
internal fun GameState.actorSeat(): Int = when (this) {
    is MonoState -> currentSeat
    is MonoChaosState -> currentSeat
    else -> 1
}

private const val NUMBER = "NumberCard"
private const val REVERSE = "ReverseCard"
private const val SKIP = "SkipCard"
private const val DRAW_TWO = "DrawTwoCard"
private const val COLORED_DRAW_FOUR = "ColoredDrawFourCard"
private const val DISCARD_ALL = "DiscardAllCard"
private const val SKIP_EVERYONE = "SkipEveryoneCard"
private const val DRAW_FOUR = "PrismDrawFour"
private const val PRISM = "PrismColorWild"
private const val REVERSE_DRAW_FOUR = "PrismReverseDrawFour"
private const val DRAW_SIX = "PrismDrawSix"
private const val DRAW_TEN = "PrismDrawTen"
private const val ROULETTE = "PrismColorRoulette"