package com.kglabs28.yapnplay.appengine.features.matchresult.domain

/** What one seat got when the match ended: [won] coins from the pot, [completion] coins for finishing, and [xp]. */
data class SeatResult(
    val seat: Int,
    val won: Int,
    val completion: Int,
    val xp: Int,
) {
    val coins: Int get() = won + completion
}
