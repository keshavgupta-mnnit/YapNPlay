package com.kglabs28.yapnplay.appengine.games.royalludo.domain

/** What the player can do at the table. */
sealed interface LudoCommand {
    data object Roll : LudoCommand
    data class Move(val piece: Int) : LudoCommand
    data class SetAutoplay(val enabled: Boolean) : LudoCommand
    data class SetMic(val on: Boolean) : LudoCommand
    /** Leave the match, giving the seat to [giveToUserId] (a spectator) or the bot (null). */
    data class Leave(val giveToUserId: String?) : LudoCommand
    /** "I'm still here": cancels the absence check after idle turns. */
    data object StillHere : LudoCommand
    /** The user, offered a seat by name, accepts or declines. */
    data class AnswerSeatOffer(val accept: Boolean) : LudoCommand
    /** The user, as leader, gives the seat to a spectator or the bot (null). */
    data class ChooseSeatTaker(val giveToUserId: String?) : LudoCommand
    /** The app went to the background (true) or came back (false). */
    data class SetAway(val away: Boolean) : LudoCommand
    /** A spectator takes a seat open to all. */
    data class TakeSeat(val seat: Int) : LudoCommand
}
