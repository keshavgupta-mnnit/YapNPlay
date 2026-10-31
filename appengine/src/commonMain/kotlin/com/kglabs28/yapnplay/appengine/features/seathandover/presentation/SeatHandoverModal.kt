package com.kglabs28.yapnplay.appengine.features.seathandover.presentation

/** A seat dialog the user owes, the same in every game. All of them need an answer (no tap-outside close). */
sealed interface SeatHandoverModal {
    /** A spectator may take a seat open to all, or pass. */
    data class SeatInvite(val seat: Int, val title: String, val body: String, val takeLabel: String) : SeatHandoverModal

    /** The bot played the user's last turns: stay, or hand the seat to the pick (bot first) and spectate. */
    data class AbsenceCheck(val title: String, val body: String, val countdown: String, val options: List<SeatPickOptionUi>, val selectedIndex: Int?) : SeatHandoverModal

    /** The user is offered a seat by name; the match waits for the answer. */
    data class SeatOfferPrompt(val title: String, val body: String, val acceptLabel: String) : SeatHandoverModal

    /** The user, as leader, invites a spectator to the seat or gives it to the bot. */
    data class LeaderSeatChoice(val title: String, val body: String, val countdown: String, val options: List<SeatPickOptionUi>, val selectedIndex: Int?) : SeatHandoverModal

    /** The leader (or admin) picks a spectator to offer a seat the bot plays. */
    data class OfferBotSeat(val title: String, val body: String, val options: List<SeatPickOptionUi>, val selectedIndex: Int?) : SeatHandoverModal
}
