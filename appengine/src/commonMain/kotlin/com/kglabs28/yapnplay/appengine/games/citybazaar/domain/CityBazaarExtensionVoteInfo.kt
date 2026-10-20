package com.kglabs28.yapnplay.appengine.games.citybazaar.domain

import kotlinx.datetime.Instant

/** A round or time limit is reached: [voters] (humans still in) decide by [endsAt] whether to play [extraRounds] more rounds or [extraMinutes] more minutes. */
data class CityBazaarExtensionVoteInfo(
    val endsAt: Instant,
    val extraRounds: Int?,
    val extraMinutes: Int?,
    val voters: List<Int>,
    val yes: List<Int>,
    val no: List<Int>,
)
