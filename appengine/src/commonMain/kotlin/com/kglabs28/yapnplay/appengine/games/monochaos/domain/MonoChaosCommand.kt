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
    /** End the turn after drawing a playable card and declining to play it. */
    data object Pass : MonoChaosCommand
    /** Call out a seat that went down to one card without calling MONO. */
    data object Caught : MonoChaosCommand
    /** The keep-playing vote. */
    data class VoteContinue(val keepPlaying: Boolean) : MonoChaosCommand
    /** The Rule of 7: pick the opponent whose hand to trade with. */
    data class ChooseSwapTarget(val seat: Int) : MonoChaosCommand
    data class SetAutoplay(val enabled: Boolean) : MonoChaosCommand
    data class SetMic(val on: Boolean) : MonoChaosCommand
    data class Leave(val giveToUserId: String?) : MonoChaosCommand
    data object StillHere : MonoChaosCommand
    data class SetAway(val away: Boolean) : MonoChaosCommand
    /** Ask everyone to end the game early (a vote). */
    data object ProposeEnd : MonoChaosCommand
    /** The user's answer to the end-game vote. */
    data class VoteEnd(val agree: Boolean) : MonoChaosCommand
}
