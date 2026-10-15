package com.kglabs28.yapnplay.shared.game.api

/**
 * Final ranking of a match.
 */
data class MatchOutcome(
    val ranking: List<RankedSeat>,
)

data class RankedSeat(
    val seat: SeatNumber,
    val status: SeatStatus,
    val place: Int,
)

enum class SeatStatus {
    FINISHED,
    REMOVED,
}
