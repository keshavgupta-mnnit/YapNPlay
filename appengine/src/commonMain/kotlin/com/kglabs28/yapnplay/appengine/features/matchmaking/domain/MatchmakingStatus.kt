package com.kglabs28.yapnplay.appengine.features.matchmaking.domain

import kotlinx.datetime.Instant

/**
 * Where a Quick Play search stands. The server decides every step; the
 * app only shows it. [Searching.searchEndsAt] is already converted to
 * this device's clock.
 */
sealed interface MatchmakingStatus {

    data class Searching(
        val gameId: String,
        val playersFound: Int,
        val playersNeeded: Int,
        val searchEndsAt: Instant,
    ) : MatchmakingStatus

    /** Seats are filled (humans first, CPU players for the rest). */
    data class Found(
        val matchId: String,
        val humanPlayers: Int,
        val cpuPlayers: Int,
    ) : MatchmakingStatus

    data class Cancelled(val reason: MatchmakingCancelReason) : MatchmakingStatus
}

enum class MatchmakingCancelReason {
    NOT_ENOUGH_PLAYERS,
    GAME_UNAVAILABLE,
    LEFT_QUEUE,
    CONNECTION_FAILED,
    /** The user can't pay the league's entry fee. */
    NOT_ENOUGH_COINS,
}
