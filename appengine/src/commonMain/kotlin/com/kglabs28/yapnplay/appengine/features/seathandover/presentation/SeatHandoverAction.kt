package com.kglabs28.yapnplay.appengine.features.seathandover.presentation

/** What the user does in a seat dialog (or a long press on a bot's seat), the same in every game. */
sealed interface SeatHandoverAction {
    data class Take(val seat: Int) : SeatHandoverAction
    data class Decline(val seat: Int) : SeatHandoverAction

    /** Picked a tile in the dialog's list. */
    data class Pick(val index: Int) : SeatHandoverAction
    data class Answer(val accept: Boolean) : SeatHandoverAction

    /** Leader: give the seat to the picked spectator. */
    data object InviteToSeat : SeatHandoverAction

    /** Leader: give the seat to the bot. */
    data object OfferToBot : SeatHandoverAction

    /** Idle player: hand the seat to the pick (bot or spectator) and keep watching. */
    data object Spectate : SeatHandoverAction
    data object StillHere : SeatHandoverAction

    /** Long press on a seat the bot plays: the leader picks a spectator for it. */
    data class OpenBotSeatOffer(val seat: Int) : SeatHandoverAction
    data object SendBotSeatOffer : SeatHandoverAction
    data object CancelBotSeatOffer : SeatHandoverAction
}
