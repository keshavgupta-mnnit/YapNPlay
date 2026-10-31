package com.kglabs28.yapnplay.appengine.games.mono.domain

import com.kglabs28.yapnplay.appengine.games.base.identity.SeatNumber
import com.kglabs28.yapnplay.appengine.games.base.play.GameEvent
import com.kglabs28.yapnplay.appengine.games.mono.identity.MonoConstants
import kotlinx.serialization.Serializable

@Serializable
sealed class MonoEvent : GameEvent {
    data class CardPlayed(val seat: SeatNumber, val card: MonoCard, val color: MonoConstants.Color?) : MonoEvent()
    data class CardDrawn(val seat: SeatNumber, val card: MonoCard) : MonoEvent()
    /** Another seat drew [count] cards (the cards themselves stay hidden). */
    data class CardsDrawn(val seat: SeatNumber, val count: Int) : MonoEvent()
    data class ColorChosen(val seat: SeatNumber, val color: MonoConstants.Color) : MonoEvent()
    data class MonoCalled(val seat: SeatNumber) : MonoEvent()
    data class MonoMissed(val seat: SeatNumber, val penaltyCards: Int) : MonoEvent()
    data class MonoCaught(val catcher: SeatNumber, val seat: SeatNumber, val penaltyCards: Int) : MonoEvent()
    data class FalseCatch(val seat: SeatNumber, val target: SeatNumber, val penaltyCards: Int) : MonoEvent()
    data class DeckReshuffled(val drawPileCount: Int) : MonoEvent()
    data class ContinueVoted(val continued: Boolean, val yes: Int, val voters: Int) : MonoEvent()
    data class TurnPassed(val seat: SeatNumber) : MonoEvent()
    data class SeatFinished(val seat: SeatNumber, val place: Int) : MonoEvent()
    data class MatchEnded(val finishingOrder: List<Int>) : MonoEvent()
}
