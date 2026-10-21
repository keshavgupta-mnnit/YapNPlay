package com.kglabs28.yapnplay.appengine.games.citybazaar.domain

import kotlinx.datetime.Instant

/** Someone asked to end the game: [voters] (humans still in) decide by [endsAt]; the proposer counts as yes. Play is paused meanwhile. */
data class CityBazaarEndVoteInfo(
    val proposerSeat: Int,
    val endsAt: Instant,
    val voters: List<Int>,
    val yes: List<Int>,
    val no: List<Int>,
)
