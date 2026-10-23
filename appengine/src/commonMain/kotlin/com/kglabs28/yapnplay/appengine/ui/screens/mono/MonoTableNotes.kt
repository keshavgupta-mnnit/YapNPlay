package com.kglabs28.yapnplay.appengine.ui.screens.mono

import com.kglabs28.yapnplay.appengine.core.ui.MonoStrings
import com.kglabs28.yapnplay.appengine.games.mono.domain.MonoEvent
import com.kglabs28.yapnplay.appengine.games.mono.domain.MonoMatch
import com.kglabs28.yapnplay.appengine.games.monochaos.domain.MonoChaosEvent
import com.kglabs28.yapnplay.appengine.games.monochaos.domain.MonoChaosMatch

/**
 * What just happened at the table, in one line for the table's banner:
 * colour changes, stacks, the roulette, dumps, catches, swaps, knockouts —
 * the moves that change things without a card the player can follow. The
 * last such event of an update wins; null when none of them happened.
 */
object MonoTableNotes {
    fun mono(events: List<MonoEvent>, m: MonoMatch): String? {
        val me = m.me?.seat
        fun name(seat: Int) = m.player(seat)?.name.orEmpty()
        return events.mapNotNull { e ->
            when (e) {
                is MonoEvent.ColorChosen -> colour(e.seat.value == me, name(e.seat.value), e.color.name)
                is MonoEvent.MonoCaught -> caught(me, e.catcher.value, e.seat.value, e.penaltyCards, ::name)
                is MonoEvent.FalseCatch -> wrongCatch(e.seat.value == me, name(e.seat.value), e.penaltyCards)
                is MonoEvent.MonoMissed -> missed(e.seat.value == me, name(e.seat.value), e.penaltyCards)
                is MonoEvent.HandSwapped -> MonoStrings.monoNoteSwapped(name(e.from.value), name(e.to.value))
                is MonoEvent.HandsPassed -> MonoStrings.monoNotePassed
                else -> null
            }
        }.lastOrNull()
    }

    fun chaos(events: List<MonoChaosEvent>, m: MonoChaosMatch): String? {
        val me = m.me?.seat
        fun name(seat: Int) = m.player(seat)?.name.orEmpty()
        return events.mapNotNull { e ->
            when (e) {
                is MonoChaosEvent.ColorChosen -> colour(e.seat.value == me, name(e.seat.value), e.color.name)
                is MonoChaosEvent.RouletteColorChosen -> {
                    val colour = MonoStrings.monoColourName(e.color.name)
                    if (e.victim.value == me) MonoStrings.monoNoteYouRoulette(colour) else MonoStrings.monoNoteRoulette(name(e.victim.value), colour)
                }
                is MonoChaosEvent.StackDraw -> if (e.seat.value == me) MonoStrings.monoNoteYouStacked(e.newStackTotal) else MonoStrings.monoNoteStacked(name(e.seat.value), e.newStackTotal)
                is MonoChaosEvent.StackAccepted -> if (e.seat.value == me) MonoStrings.monoNoteYouTookStack(e.penaltyCards) else MonoStrings.monoNoteTookStack(name(e.seat.value), e.penaltyCards)
                is MonoChaosEvent.CardsDiscarded -> if (e.seat.value == me) MonoStrings.monoNoteYouDumped(e.count) else MonoStrings.monoNoteDumped(name(e.seat.value), e.count)
                is MonoChaosEvent.MonoCaught -> caught(me, e.catcher.value, e.seat.value, e.penaltyCards, ::name)
                is MonoChaosEvent.FalseCatch -> wrongCatch(e.seat.value == me, name(e.seat.value), e.penaltyCards)
                is MonoChaosEvent.MonoMissed -> missed(e.seat.value == me, name(e.seat.value), e.penaltyCards)
                is MonoChaosEvent.HandSwapped -> MonoStrings.monoNoteSwapped(name(e.from.value), name(e.to.value))
                is MonoChaosEvent.HandsPassed -> MonoStrings.monoNotePassed
                is MonoChaosEvent.PlayerEliminated -> if (e.seat.value == me) MonoStrings.monoNoteYouKnockedOut else MonoStrings.monoNoteKnockedOut(name(e.seat.value))
                else -> null
            }
        }.lastOrNull()
    }

    private fun colour(mine: Boolean, name: String, colour: String): String {
        val shown = MonoStrings.monoColourName(colour)
        return if (mine) MonoStrings.monoNoteYouColour(shown) else MonoStrings.monoNoteColour(name, shown)
    }

    private fun caught(me: Int?, catcher: Int, caught: Int, count: Int, name: (Int) -> String): String = when (me) {
        caught -> MonoStrings.monoNoteYouWereCaught(name(catcher), count)
        catcher -> MonoStrings.monoNoteYouCaught(name(caught), count)
        else -> MonoStrings.monoNoteCaught(name(catcher), name(caught), count)
    }

    private fun wrongCatch(mine: Boolean, name: String, count: Int) =
        if (mine) MonoStrings.monoNoteYouWrongCatch(count) else MonoStrings.monoNoteWrongCatch(name, count)

    private fun missed(mine: Boolean, name: String, count: Int) =
        if (mine) MonoStrings.monoNoteYouMissed(count) else MonoStrings.monoNoteMissed(name, count)
}
