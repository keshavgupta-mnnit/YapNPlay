package com.kglabs28.yapnplay.appengine.games.monochaos.data.remote.wire

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** MONO: Complete Chaos actions inside `GameAction.action`; `"type"` names the action. */
@Serializable
sealed interface MonoChaosWireAction {
    @Serializable @SerialName("PlayCard") data class PlayCard(val handIndex: Int) : MonoChaosWireAction
    @Serializable @SerialName("DrawUntilPlayable") data object DrawUntilPlayable : MonoChaosWireAction
    /** [color] is a MonoChaosConstants.Color name. */
    @Serializable @SerialName("ChooseColor") data class ChooseColor(val color: String) : MonoChaosWireAction
    @Serializable @SerialName("ChooseRouletteColor") data class ChooseRouletteColor(val color: String) : MonoChaosWireAction
    @Serializable @SerialName("CallMono") data object CallMono : MonoChaosWireAction
    @Serializable @SerialName("StackDraw") data class StackDraw(val handIndex: Int) : MonoChaosWireAction
    @Serializable @SerialName("AcceptStack") data object AcceptStack : MonoChaosWireAction
    @Serializable @SerialName("Pass") data object Pass : MonoChaosWireAction
    /** The Rule of 7: the seat whose hand to trade with. */
    @Serializable @SerialName("ChooseSwapTarget") data class ChooseSwapTarget(val seat: Int) : MonoChaosWireAction
}