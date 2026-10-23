package com.kglabs28.yapnplay.appengine.games.base.match

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/**
 * Someone asked to end the game early (any game): [voters] (the humans
 * still in) decide by [endsAt]; the proposer counts as yes. Play is
 * paused meanwhile. It ends the game if more than half say yes.
 */
@Serializable
data class MatchEndVote(
    val proposerSeat: Int,
    val endsAt: Instant,
    val voters: List<Int>,
    val yes: List<Int>,
    val no: List<Int>,
)
