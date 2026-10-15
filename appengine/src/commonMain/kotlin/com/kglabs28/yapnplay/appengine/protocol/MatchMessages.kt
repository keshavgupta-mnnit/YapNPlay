package com.kglabs28.yapnplay.appengine.protocol

import com.kglabs28.yapnplay.appengine.games.common.gameplay.moves.LegalMove
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

// ══════════════════════════════════════════════════════════════════════════
// Match channel — client to server requests
// (Mechanic-specific requests like RollDice/MovePiece live in
// games/common/protocol — this file holds only generic messages.)
// ══════════════════════════════════════════════════════════════════════════

/** Request a match snapshot (used on reconnect). */
@Serializable
data class MatchRequestSnapshot(
    val matchId: String,
    val lastVersion: Long,
)

// ══════════════════════════════════════════════════════════════════════════
// Match channel — server to client responses
// ══════════════════════════════════════════════════════════════════════════

/** Full match state snapshot, sent after every match change. */
@Serializable
data class MatchSnapshot(
    val matchId: String,
    val version: Long,
    /** Server time in epoch milliseconds. */
    val serverTime: Long,
    /** Who is viewing: player with seat, or spectator. */
    val viewer: ViewerInfo,
    val seats: List<SeatInfo>,
    /** Game-specific public state as JSON (deserialized by the game module). */
    val publicState: JsonObject,
    /** Legal moves for the acting seat. */
    val legalMoves: List<LegalMove>,
    val turn: TurnInfo,
)

@Serializable
data class ViewerInfo(
    val role: ViewerRole,
    /** Seat number if viewer is a player, null if spectator. */
    val seat: Int? = null,
)

enum class ViewerRole {
    PLAYER,
    SPECTATOR,
}

@Serializable
data class SeatInfo(
    val seat: Int,
    val colour: Colour,
    val controllerUserId: String,
    val status: MatchSeatStatus,
    val connected: Boolean,
    val autoPlay: Boolean,
    val consecutiveMisses: Int,
)

enum class Colour {
    RED,
    GREEN,
    YELLOW,
    BLUE,
}

enum class MatchSeatStatus {
    ACTIVE,
    REMOVED,
}

/** A single game event with its version (for the event log). */
@Serializable
data class MatchEvent(
    val version: Long,
    /** Game-specific event as JSON. */
    val event: JsonObject,
)

/** Sent when the match ends with the final ranking. */
@Serializable
data class MatchEnded(
    val ranking: List<RankedSeat>,
)

@Serializable
data class RankedSeat(
    val seat: Int,
    val status: MatchSeatStatus,
    /** 1-based finishing place. */
    val place: Int,
)

// ══════════════════════════════════════════════════════════════════════════
// Shared types
// ══════════════════════════════════════════════════════════════════════════

/** The current turn: whose seat, what phase, and when the deadline is. */
@Serializable
data class TurnInfo(
    val seat: Int,
    /** Game-specific phase (e.g., "ROLL" or "MOVE" for Royal Ludo). */
    val phase: String,
    /** Absolute deadline in epoch milliseconds. */
    val deadline: Long,
)


