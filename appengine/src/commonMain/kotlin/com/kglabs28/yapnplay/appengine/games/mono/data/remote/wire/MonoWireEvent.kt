package com.kglabs28.yapnplay.appengine.games.mono.data.remote.wire

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Standard MONO events in `MatchSnapshot.events`; `"type"` names the event. */
@Serializable
sealed interface MonoWireEvent {

    @Serializable @SerialName("CardPlayed")
    data class CardPlayed(val seat: Int, val card: MonoCardWire, val color: String? = null) : MonoWireEvent

    @Serializable @SerialName("CardDrawn")
    data class CardDrawn(val seat: Int, val card: MonoCardWire) : MonoWireEvent

    /** What everyone else sees of a draw: only how many (consecutive draws merged). */
    @Serializable @SerialName("CardsDrawn")
    data class CardsDrawn(val seat: Int, val count: Int) : MonoWireEvent

    /** [color] is a MonoConstants.Color name. */
    @Serializable @SerialName("ColorChosen")
    data class ColorChosen(val seat: Int, val color: String) : MonoWireEvent

    @Serializable @SerialName("MonoCalled")
    data class MonoCalled(val seat: Int) : MonoWireEvent

    @Serializable @SerialName("MonoMissed")
    data class MonoMissed(val seat: Int, val penaltyCards: Int) : MonoWireEvent

    /** [catcher] caught [seat] on one card without a call; [seat] drew [penaltyCards]. */
    @Serializable @SerialName("MonoCaught")
    data class MonoCaught(val catcher: Int, val seat: Int, val penaltyCards: Int) : MonoWireEvent

    /** [seat] pressed CAUGHT on [target], who had called; [seat] drew [penaltyCards]. */
    @Serializable @SerialName("FalseCatch")
    data class FalseCatch(val seat: Int, val target: Int, val penaltyCards: Int) : MonoWireEvent

    /** The draw pile ran out: the discard pile (all but its top card) was shuffled into a new one. */
    /** The keep-playing vote closed: [continued] or the match ends. */
    @Serializable @SerialName("ContinueVoted")
    data class ContinueVoted(val continued: Boolean, val yes: Int, val voters: Int) : MonoWireEvent

    @Serializable @SerialName("DeckReshuffled")
    data class DeckReshuffled(val drawPileCount: Int) : MonoWireEvent



    @Serializable @SerialName("TurnPassed")
    data class TurnPassed(val seat: Int) : MonoWireEvent

    @Serializable @SerialName("SeatFinished")
    data class SeatFinished(val seat: Int, val place: Int) : MonoWireEvent

    @Serializable @SerialName("MatchEnded")
    data class MatchEnded(val finishingOrder: List<Int>) : MonoWireEvent
}