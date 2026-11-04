package com.kglabs28.yapnplay.appengine.games.base.match

import kotlinx.datetime.Instant

/** The keep-playing vote at a game's limit: who may vote, who said yes or no, and what going on adds. */
data class MatchKeepPlayingVote(
    val endsAt: Instant,
    val voters: List<Int>,
    val yes: List<Int>,
    val no: List<Int>,
    val extraRounds: Int?,
    val extraMinutes: Int?,
)
