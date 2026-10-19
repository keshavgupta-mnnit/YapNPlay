package com.kglabs28.yapnplay.appengine.games.mono.domain

import com.kglabs28.yapnplay.appengine.games.base.play.Action
import com.kglabs28.yapnplay.appengine.games.mono.identity.MonoConstants
import kotlinx.serialization.Serializable

@Serializable
sealed class MonoAction : Action {
    data class PlayCard(val handIndex: Int) : MonoAction()
    data object DrawCard : MonoAction()
    data class ChooseColor(val color: MonoConstants.Color) : MonoAction()
    data object CallMono : MonoAction()
    data object AcceptStack : MonoAction()
}
