package com.kglabs28.yapnplay.appengine.games.mono.data.remote.wire

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Standard MONO actions inside `GameAction.action`; `"type"` names the action. */
@Serializable
sealed interface MonoWireAction {
    @Serializable @SerialName("PlayCard") data class PlayCard(val handIndex: Int) : MonoWireAction
    @Serializable @SerialName("DrawCard") data object DrawCard : MonoWireAction
    /** [color] is a MonoConstants.Color name. */
    @Serializable @SerialName("ChooseColor") data class ChooseColor(val color: String) : MonoWireAction
    @Serializable @SerialName("CallMono") data object CallMono : MonoWireAction
    @Serializable @SerialName("AcceptStack") data object AcceptStack : MonoWireAction
    @Serializable @SerialName("Pass") data object Pass : MonoWireAction
    /** Calls out a seat that went down to one card without calling MONO. */
    @Serializable @SerialName("Caught") data object Caught : MonoWireAction
    /** The keep-playing vote: true keeps the match going. */
    @Serializable @SerialName("VoteContinue") data class VoteContinue(val keepPlaying: Boolean) : MonoWireAction
    /** The Rule of 7: the seat whose hand to trade with. */
    @Serializable @SerialName("ChooseSwapTarget") data class ChooseSwapTarget(val seat: Int) : MonoWireAction
}