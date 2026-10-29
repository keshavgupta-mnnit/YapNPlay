package com.kglabs28.yapnplay.appengine.features.matchresult.domain

/** What one seat got when the match ended: [won] coins from the pot, [completion] coins for finishing, and [xp]. */
data class SeatResult(
    val seat: Int,
    val won: Int,
    val completion: Int,
    val xp: Int,
    /** The entry put in; lost when nothing was won. */
    val stake: Int = 0,
) {
    val coins: Int get() = won + completion
    /** What the match came to for this seat: prize and bonus, less the entry. */
    val net: Int get() = coins - stake
}
