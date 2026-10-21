package com.kglabs28.yapnplay.appengine.games.mono.domain

/** What the player can do at the MONO table. */
sealed interface MonoCommand {
    data object Roll : MonoCommand
    data class PlayCard(val handIndex: Int) : MonoCommand
    data object DrawCard : MonoCommand
    data class ChooseColor(val color: String) : MonoCommand
    data object CallMono : MonoCommand
    /** Take the penalty waiting on the table instead of stacking onto it. */
    data object AcceptStack : MonoCommand
    /** End the turn after drawing a playable card and declining to play it. */
    data object Pass : MonoCommand
    /** The Rule of 7: pick the opponent whose hand to trade with. */
    data class ChooseSwapTarget(val seat: Int) : MonoCommand
    data class SetAutoplay(val enabled: Boolean) : MonoCommand
    data class SetMic(val on: Boolean) : MonoCommand
    data class Leave(val giveToUserId: String?) : MonoCommand
    data object StillHere : MonoCommand
    data class AnswerSeatOffer(val accept: Boolean) : MonoCommand
    data class ChooseSeatTaker(val giveToUserId: String?) : MonoCommand
    data class SetAway(val away: Boolean) : MonoCommand
    data class TakeSeat(val seat: Int) : MonoCommand
}
