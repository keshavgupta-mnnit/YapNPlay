package com.kglabs28.yapnplay.appengine.games.doodleking.domain

/** The user's moves at the table. */
sealed interface DoodleCommand {
    data class PickWord(val index: Int) : DoodleCommand
    data class Guess(val text: String) : DoodleCommand
    data object Pass : DoodleCommand
    data class SetMic(val on: Boolean) : DoodleCommand
    /** Leaves the match; [giveToUserId] takes the seat (a spectator), null: a CPU or nobody. */
    data class Leave(val giveToUserId: String?) : DoodleCommand
    data object ProposeEnd : DoodleCommand
    data class VoteEnd(val agree: Boolean) : DoodleCommand
    data class VoteKeepPlaying(val keepPlaying: Boolean) : DoodleCommand
}
