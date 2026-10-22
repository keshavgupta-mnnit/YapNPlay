package com.kglabs28.yapnplay.appengine.ui.screens.mono

/** The keep-playing vote, ready to show: the countdown, the tally, and whether the user still has a vote. */
data class MonoVoteUi(
    val secondsLeft: Long,
    val yes: Int,
    val no: Int,
    val voters: Int,
    val canVote: Boolean,
)
