package com.kglabs28.yapnplay.appengine.features.seathandover.domain

/**
 * Where a seat handover stands. Play pauses ([holdsMatch]) only while
 * someone decides on a seat for someone else: the leader choosing, or a
 * named spectator answering. Otherwise the game goes on (the bot plays an
 * empty seat).
 */
sealed interface SeatOfferStage {
    val holdsMatch: Boolean

    /**
     * The bot has played [userId]'s last turns on timeout: they are asked
     * whether they are still there before their seat is offered.
     */
    data class ConfirmingAbsence(val userId: String, val name: String) : SeatOfferStage {
        override val holdsMatch: Boolean get() = false
    }

    /** The named spectator may accept or decline. [round] 1: the leaver's pick; 2: the leader's pick. */
    data class ToSpectator(val userId: String, val name: String, val round: Int) : SeatOfferStage {
        override val holdsMatch: Boolean get() = true
    }

    /** The leader (or whoever stands in for them) picks a spectator or the bot. */
    data class LeaderChoosing(val leaderUserId: String, val leaderName: String) : SeatOfferStage {
        override val holdsMatch: Boolean get() = true
    }

    /** The owner went away: any spectator may take it while the bot plays it. */
    data object OpenToAll : SeatOfferStage {
        override val holdsMatch: Boolean get() = false
    }
}
