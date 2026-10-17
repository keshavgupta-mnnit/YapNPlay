package com.kglabs28.yapnplay.appengine.features.seathandover.domain

/**
 * How a seat is handed on mid-match, for every game. Room matches only:
 * Quick Play has no spectators, so a leaving player's seat goes to the bot.
 *
 * 1. On exit, the player picks the bot (default) or a spectator.
 * 2. A picked spectator has [SPECTATOR_ANSWER_SECONDS] to accept; the
 *    match waits. If they decline or don't answer, the leader picks
 *    within [LEADER_CHOICE_SECONDS], and the match waits for that too.
 * 3. The leader's spectator gets the same window. A decline, no answer,
 *    or no pick from the leader hands the seat to the bot.
 *
 * A player whose last [IDLE_TURNS_BEFORE_CHECK] whole turns were played
 * by the bot on timeout (Autoplay off; extra rolls in one turn count
 * once) is asked "still here?" for [ABSENCE_CHECK_SECONDS]. No answer:
 * the leader picks (step 2 on), or in Quick Play the bot takes the seat.
 *
 * The real values live in the server's game config; the fakes use these.
 */
object SeatHandoverRules {
    const val SPECTATOR_ANSWER_SECONDS = 30L
    const val LEADER_CHOICE_SECONDS = 10L
    const val IDLE_TURNS_BEFORE_CHECK = 3
    const val ABSENCE_CHECK_SECONDS = 15L

    /** Who decides in step 2: the room leader if still present, else whoever has been online longest. */
    fun decider(roomLeaderId: String?, present: List<PresentMember>): PresentMember? =
        present.firstOrNull { it.userId == roomLeaderId } ?: present.minByOrNull { it.onlineSince }
}
