package com.kglabs28.yapnplay.appengine.features.seathandover.domain

/** A seat handover request, the same in every game. The server checks it is allowed. */
sealed interface SeatCommand {
    /** The user, asked after idle turns, is still playing. */
    data object StillHere : SeatCommand

    /** The named spectator's answer to a seat offered to them. */
    data class Answer(val accept: Boolean) : SeatCommand

    /** The leader's pick for a seat being handed on; null gives it to the bot. */
    data class ChooseTaker(val giveToUserId: String?) : SeatCommand

    /** A spectator takes a seat open to all. */
    data class Take(val seat: Int) : SeatCommand

    /** The user leaves their seat to [giveToUserId] (null: the bot) and keeps watching. */
    data class Leave(val giveToUserId: String?) : SeatCommand

    /** The leader (or admin) offers a seat the bot plays to a spectator, who still has to accept. */
    data class OfferBotSeat(val seat: Int, val toUserId: String) : SeatCommand
}
