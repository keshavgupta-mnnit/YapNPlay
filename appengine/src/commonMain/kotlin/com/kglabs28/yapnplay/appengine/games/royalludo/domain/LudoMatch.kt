package com.kglabs28.yapnplay.appengine.games.royalludo.domain

import kotlinx.datetime.Instant

/**
 * The live match, rebuilt from every server snapshot. Piece progress per
 * seat follows the engine: -1 base, 0..50 shared track, 51..55 home
 * column, 56 finished. [turnEndsAt] is already on this device's clock.
 */
data class LudoMatch(
    val matchId: String,
    val title: String,
    val players: List<LudoPlayer>,
    val spectators: List<LudoSpectator>,
    /** How many people are watching right now (the server counts them). */
    val spectatorCount: Int,
    val pieces: Map<Int, List<Int>>,
    val currentSeat: Int,
    val phase: LudoPhase,
    val lastRoll: Int?,
    val legalPieces: List<Int>,
    val turnEndsAt: Instant?,
    /** Seats in finishing order once the match is over. */
    val ranking: List<Int>,
) {
    val me: LudoPlayer? get() = players.firstOrNull { it.isMe }
}
