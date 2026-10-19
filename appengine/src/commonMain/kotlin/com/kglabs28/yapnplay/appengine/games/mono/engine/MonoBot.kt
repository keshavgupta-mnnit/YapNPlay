package com.kglabs28.yapnplay.appengine.games.mono.engine

import com.kglabs28.yapnplay.appengine.games.base.identity.SeatNumber
import com.kglabs28.yapnplay.appengine.games.base.match.GameState
import com.kglabs28.yapnplay.appengine.games.base.play.*
import com.kglabs28.yapnplay.appengine.games.mono.domain.*
import com.kglabs28.yapnplay.appengine.games.mono.domain.isPlayableOn
import com.kglabs28.yapnplay.appengine.games.mono.identity.MonoConstants
import kotlinx.serialization.Serializable

class MonoBot : GameBot {
    override fun chooseAction(state: GameState, seat: SeatNumber): Action? {
        val s = state as MonoState
        val hand = s.handForSeat(seat.value)
        val playable = hand.withIndex()
            .filter { (_, card) -> isPlayable(card, s.discardPile.lastOrNull(), s.topCardColor) }

        if (playable.isNotEmpty()) {
            return MonoAction.PlayCard(playable.first().index)
        }

        if (s.drawPile.isNotEmpty()) {
            return MonoAction.DrawCard
        }

        return null
    }

    private fun isPlayable(card: MonoCard, topCard: MonoCard?, topColor: MonoConstants.Color?): Boolean =
        card.isPlayableOn(topCard, topColor)
}
