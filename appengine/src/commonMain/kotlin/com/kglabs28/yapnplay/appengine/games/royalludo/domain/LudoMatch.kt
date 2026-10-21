package com.kglabs28.yapnplay.appengine.games.royalludo.domain

import com.kglabs28.yapnplay.appengine.features.seathandover.domain.SeatOffer
import kotlinx.datetime.Instant

/**
 * The live match, rebuilt from every server snapshot. Piece progress per
 * seat follows the engine: -1 base, 0..50 shared track, 51..55 home
 * column, 56 finished. [turnEndsAt] and [startedAt] are already on this
 * device's clock.
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
    /** When the match began; the top bar shows the time since. */
    val startedAt: Instant,
    /** Seats that have brought all pieces home, in order (1st, 2nd…), during the match. */
    val finishedSeats: List<Int>,
    /** Seats in finishing order once the match is over. */
    val ranking: List<Int>,
    /** The coin pot of a paid league (0 for free games). */
    val pot: Int = 0,
    /** A seat changing hands (room matches only); null when none. */
    val seatOffer: SeatOffer? = null,
    /** The room this match belongs to; null for Quick Play (no spectators, no handover). */
    val roomId: String? = null,
    /** Who is watching this table on this device (player or spectator). */
    val viewerUserId: String = "",
) {
    val me: LudoPlayer? get() = players.firstOrNull { it.isMe }
    val isRoomMatch: Boolean get() = roomId != null
    /** Play waits while a seat is offered to someone by name or the leader is choosing. */
    val onHold: Boolean get() = seatOffer?.stage?.holdsMatch == true
}
