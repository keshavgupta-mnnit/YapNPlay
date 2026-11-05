package com.kglabs28.yapnplay.appengine.ui.screens.mono

import com.kglabs28.yapnplay.appengine.ui.components.seat.VoteVotersUi
/** The keep-playing vote, ready to show: the countdown, the tally, and whether the user still has a vote. */
data class MonoVoteUi(
    val secondsLeft: Long,
    val yes: Int,
    val no: Int,
    val voters: Int,
    val canVote: Boolean,
    /** Who voted to keep playing (yes) and to end (no). */
    val who: VoteVotersUi = VoteVotersUi(),
)
