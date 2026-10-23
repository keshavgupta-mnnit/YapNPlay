package com.kglabs28.yapnplay.appengine.games.monochaos.engine

import com.kglabs28.yapnplay.appengine.games.base.identity.SeatNumber
import com.kglabs28.yapnplay.appengine.games.base.match.GameState
import com.kglabs28.yapnplay.appengine.games.base.play.*
import com.kglabs28.yapnplay.appengine.games.monochaos.domain.*
import com.kglabs28.yapnplay.appengine.games.monochaos.identity.MonoChaosConstants
import kotlinx.serialization.Serializable

class MonoChaosBot : GameBot {
    override fun chooseAction(state: GameState, seat: SeatNumber): Action? {
        val s = state as MonoChaosState

        // If there's an active stack, try to stack or accept
        if (s.activeStackPenalty > 0) {
            val hand = s.handForSeat(seat.value)
            // Stack any draw card worth at least the last one (colour does not matter)
            val stackable = hand.withIndex()
                .filter { (_, card) -> card.penaltyValue() >= s.stackTopValue && card.penaltyValue() > 0 }
            if (stackable.isNotEmpty()) {
                return MonoChaosAction.StackDraw(stackable.first().index)
            }
            // Can't stack - accept penalty
            return MonoChaosAction.AcceptStack
        }

        // Find playable cards
        val hand = s.handForSeat(seat.value)
        val playable = hand.withIndex()
            .filter { (_, card) -> isPlayable(card, s.discardPile.lastOrNull(), s.topCardColor) }

        if (playable.isNotEmpty()) {
            // Prefer playing special cards over numbers
            val specialFirst = playable.filter { (_, card) ->
                card !is MonoChaosCard.NumberCard
            }
            val target = if (specialFirst.isNotEmpty()) specialFirst.first() else playable.first()
            return MonoChaosAction.PlayCard(target.index)
        }

        // No playable card - draw until playable
        if (s.drawPile.isNotEmpty()) {
            return MonoChaosAction.DrawUntilPlayable
        }

        return null
    }

    private fun isPlayable(card: MonoChaosCard, topCard: MonoChaosCard?, topColor: MonoChaosConstants.Color?): Boolean {
        if (card.isPrism()) return true
        val effectiveColor = topColor ?: topCard?.playColor()
        if (effectiveColor == null) return false
        return when (card) {
            is MonoChaosCard.NumberCard -> card.color == effectiveColor || card.number == (topCard as? MonoChaosCard.NumberCard)?.number
            is MonoChaosCard.ReverseCard -> card.color == effectiveColor || topCard is MonoChaosCard.ReverseCard
            is MonoChaosCard.SkipCard -> card.color == effectiveColor || topCard is MonoChaosCard.SkipCard
            is MonoChaosCard.DrawTwoCard -> card.color == effectiveColor || topCard is MonoChaosCard.DrawTwoCard
            is MonoChaosCard.ColoredDrawFourCard -> card.color == effectiveColor || topCard is MonoChaosCard.ColoredDrawFourCard
            is MonoChaosCard.DiscardAllCard -> card.color == effectiveColor || topCard is MonoChaosCard.DiscardAllCard
            is MonoChaosCard.SkipEveryoneCard -> card.color == effectiveColor || topCard is MonoChaosCard.SkipEveryoneCard
            else -> false
        }
    }
}
