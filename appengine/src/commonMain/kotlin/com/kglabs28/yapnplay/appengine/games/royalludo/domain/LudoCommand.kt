package com.kglabs28.yapnplay.appengine.games.royalludo.domain

/** What the player can do at the table. */
sealed interface LudoCommand {
    data object Roll : LudoCommand
    data class Move(val piece: Int) : LudoCommand
    data class SetAutoplay(val enabled: Boolean) : LudoCommand
    data class SetMic(val on: Boolean) : LudoCommand
    /** Leave the match, giving the seat to [giveToUserId] (a spectator) or the bot (null). */
    data class Leave(val giveToUserId: String?) : LudoCommand
    /** The app went to the background (true) or came back (false). */
    data class SetAway(val away: Boolean) : LudoCommand
    /** Ask everyone to end the game early (a vote). */
    data object ProposeEnd : LudoCommand
    /** The user's answer to the end-game vote. */
    data class VoteEnd(val agree: Boolean) : LudoCommand
}
