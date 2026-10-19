package com.kglabs28.yapnplay.appengine.games.monochaos.domain

import com.kglabs28.yapnplay.appengine.games.base.play.Action
import com.kglabs28.yapnplay.appengine.games.monochaos.identity.MonoChaosConstants
import kotlinx.serialization.Serializable

@Serializable
sealed class MonoChaosAction : Action {
    data class PlayCard(val handIndex: Int) : MonoChaosAction()
    data object DrawUntilPlayable : MonoChaosAction()
    data class ChooseColor(val color: MonoChaosConstants.Color) : MonoChaosAction()
    data class ChooseRouletteColor(val color: MonoChaosConstants.Color) : MonoChaosAction()
    data object CallMono : MonoChaosAction()
    data class StackDraw(val handIndex: Int) : MonoChaosAction()
    data object AcceptStack : MonoChaosAction()
}
