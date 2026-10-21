package com.kglabs28.yapnplay.appengine.features.matchresult.domain

/** What one seat earned when the match ended: [won] from the pot, [completion] for finishing. */
data class SeatCoins(
    val seat: Int,
    val won: Int,
    val completion: Int,
) {
    val total: Int get() = won + completion
}
