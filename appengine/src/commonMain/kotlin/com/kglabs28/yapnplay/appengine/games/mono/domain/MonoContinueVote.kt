package com.kglabs28.yapnplay.appengine.games.mono.domain

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/**
 * The keep-playing vote, open after the second (and every later) finisher:
 * [voters] (humans still seated) decide by [endsAt]; play waits meanwhile.
 * Shared by both MONO games.
 */
@Serializable
data class MonoContinueVote(
    val endsAt: Instant,
    val voters: List<Int>,
    val yes: List<Int>,
    val no: List<Int>,
)
