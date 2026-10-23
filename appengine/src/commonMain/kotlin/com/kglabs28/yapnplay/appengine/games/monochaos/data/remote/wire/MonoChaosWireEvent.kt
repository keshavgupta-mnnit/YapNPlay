package com.kglabs28.yapnplay.appengine.games.monochaos.data.remote.wire

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** MONO: Complete Chaos events in `MatchSnapshot.events`; `"type"` names the event. */
@Serializable
sealed interface MonoChaosWireEvent {

    @Serializable @SerialName("CardPlayed")
    data class CardPlayed(val seat: Int, val card: MonoChaosCardWire, val color: String? = null) : MonoChaosWireEvent

    @Serializable @SerialName("CardDrawn")
    data class CardDrawn(val seat: Int, val card: MonoChaosCardWire) : MonoChaosWireEvent

    @Serializable @SerialName("CardsDrawn")
    data class CardsDrawn(val seat: Int, val count: Int) : MonoChaosWireEvent

    /** Discard All: [count] more cards of that colour went under it. */
    @Serializable @SerialName("CardsDiscarded")
    data class CardsDiscarded(val seat: Int, val count: Int) : MonoChaosWireEvent

    /** [color] is a MonoChaosConstants.Color name. */
    @Serializable @SerialName("ColorChosen")
    data class ColorChosen(val seat: Int, val color: String) : MonoChaosWireEvent

    @Serializable @SerialName("RouletteColorChosen")
    data class RouletteColorChosen(val victim: Int, val color: String) : MonoChaosWireEvent

    @Serializable @SerialName("StackDraw")
    data class StackDraw(val seat: Int, val card: MonoChaosCardWire, val newStackTotal: Int) : MonoChaosWireEvent

    @Serializable @SerialName("StackAccepted")
    data class StackAccepted(val seat: Int, val penaltyCards: Int) : MonoChaosWireEvent

    @Serializable @SerialName("MonoCalled")
    data class MonoCalled(val seat: Int) : MonoChaosWireEvent

    @Serializable @SerialName("MonoMissed")
    data class MonoMissed(val seat: Int, val penaltyCards: Int) : MonoChaosWireEvent

    @Serializable @SerialName("MonoCaught")
    data class MonoCaught(val catcher: Int, val seat: Int, val penaltyCards: Int) : MonoChaosWireEvent

    @Serializable @SerialName("FalseCatch")
    data class FalseCatch(val seat: Int, val target: Int, val penaltyCards: Int) : MonoChaosWireEvent

    /** The keep-playing vote closed: [continued] or the match ends. */
    @Serializable @SerialName("ContinueVoted")
    data class ContinueVoted(val continued: Boolean, val yes: Int, val voters: Int) : MonoChaosWireEvent

    @Serializable @SerialName("DeckReshuffled")
    data class DeckReshuffled(val drawPileCount: Int) : MonoChaosWireEvent

    @Serializable @SerialName("HandSwapped")
    data class HandSwapped(val from: Int, val to: Int) : MonoChaosWireEvent

    @Serializable @SerialName("HandsPassed")
    data class HandsPassed(val direction: String) : MonoChaosWireEvent

    @Serializable @SerialName("PlayerEliminated")
    data class PlayerEliminated(val seat: Int) : MonoChaosWireEvent

    @Serializable @SerialName("TurnPassed")
    data class TurnPassed(val seat: Int) : MonoChaosWireEvent

    @Serializable @SerialName("SeatFinished")
    data class SeatFinished(val seat: Int, val place: Int) : MonoChaosWireEvent

    @Serializable @SerialName("MatchEnded")
    data class MatchEnded(val finishingOrder: List<Int>) : MonoChaosWireEvent
}