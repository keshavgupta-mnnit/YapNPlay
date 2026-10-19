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

    /** [color] is a MonoConstants.Color name. */
    @Serializable @SerialName("ColorChosen")
    data class ColorChosen(val seat: Int, val color: String) : MonoWireEvent

    @Serializable @SerialName("MonoCalled")
    data class MonoCalled(val seat: Int) : MonoWireEvent

    @Serializable @SerialName("MonoMissed")
    data class MonoMissed(val seat: Int, val penaltyCards: Int) : MonoWireEvent

    /** The Rule of 7: two seats traded hands. */
    @Serializable @SerialName("HandSwapped")
    data class HandSwapped(val from: Int, val to: Int) : MonoWireEvent

    /** The Rule of 0: every hand moved one way around the table. */
    @Serializable @SerialName("HandsPassed")
    data class HandsPassed(val direction: String) : MonoWireEvent

    @Serializable @SerialName("TurnPassed")
    data class TurnPassed(val seat: Int) : MonoWireEvent

    @Serializable @SerialName("SeatFinished")
    data class SeatFinished(val seat: Int, val place: Int) : MonoWireEvent

    @Serializable @SerialName("MatchEnded")
    data class MatchEnded(val finishingOrder: List<Int>) : MonoWireEvent
}