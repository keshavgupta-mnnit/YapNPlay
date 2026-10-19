package com.kglabs28.yapnplay.appengine.games.monochaos.domain

/** What the player can do at the MONO: Complete Chaos table. */
sealed interface MonoChaosCommand {
    data object Roll : MonoChaosCommand
    data class PlayCard(val handIndex: Int) : MonoChaosCommand
    data object DrawUntilPlayable : MonoChaosCommand
    data class ChooseColor(val color: String) : MonoChaosCommand
    data class ChooseRouletteColor(val color: String) : MonoChaosCommand
    data object CallMono : MonoChaosCommand
    data class StackDraw(val handIndex: Int) : MonoChaosCommand
    data object AcceptStack : MonoChaosCommand
    data class SetAutoplay(val enabled: Boolean) : MonoChaosCommand
    data class SetMic(val on: Boolean) : MonoChaosCommand
    data class Leave(val giveToUserId: String?) : MonoChaosCommand
    data object StillHere : MonoChaosCommand
    data class AnswerSeatOffer(val accept: Boolean) : MonoChaosCommand
    data class ChooseSeatTaker(val giveToUserId: String?) : MonoChaosCommand
    data class SetAway(val away: Boolean) : MonoChaosCommand
    data class TakeSeat(val seat: Int) : MonoChaosCommand
}
