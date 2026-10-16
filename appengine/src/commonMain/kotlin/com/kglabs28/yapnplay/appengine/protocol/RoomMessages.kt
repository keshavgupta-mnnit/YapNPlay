package com.kglabs28.yapnplay.appengine.protocol

import com.kglabs28.yapnplay.appengine.protocol.ErrorCode
import kotlinx.serialization.Serializable

// ══════════════════════════════════════════════════════════════════════════
// Room channel — client to server requests
// ══════════════════════════════════════════════════════════════════════════

/** Create a new room. [name] is optional. */
@Serializable
data class CreateRoom(
    val name: String? = null,
)

/** Join a room by its six-character code. Also used to re-enter a room you already belong to. */
@Serializable
data class JoinRoom(
    val code: String,
)

/** Give up membership in the room. */
@Serializable
data object LeaveRoom

/** Stay a member but stop being present (go offline in the room). */
@Serializable
data object ExitLobby

/** Leader picks the game for the next match (same picker as Game Zone). */
@Serializable
data class SelectGame(
    val gameId: String,
)

/**
 * Leader's room option, shown first in the house-rules section: when on,
 * bots fill empty seats only up to the selected game's minPlayers (never
 * beyond). When off, the match needs minPlayers ready humans.
 */
@Serializable
data class SetBotFill(
    val enabled: Boolean,
)

/** Set your ready status. */
@Serializable
data class SetReady(
    val ready: Boolean,
)

/** Start the 10-second countdown to match start. */
@Serializable
data object StartCountdown

/** Cancel an in-progress countdown. */
@Serializable
data object CancelCountdown

/** Request the latest room snapshot (or a specific version). */
@Serializable
data class RequestSnapshot(
    val lastVersion: Long,
)

// ══════════════════════════════════════════════════════════════════════════
// Room channel — server to client responses
// ══════════════════════════════════════════════════════════════════════════

/** Acknowledges a request. */
@Serializable
data class Ack(
    val requestId: String,
)

/** Rejects a request with a typed error code. */
@Serializable
data class Rejected(
    val requestId: String,
    val code: ErrorCode,
    val message: String,
)

/** Full room state snapshot, sent after every room change. */
@Serializable
data class RoomSnapshot(
    val version: Long,
    /** Server time in epoch milliseconds. Deadlines are absolute server times. */
    val serverTime: Long,
    val room: RoomInfo,
)

@Serializable
data class RoomInfo(
    val id: String,
    /** Six-character join code. */
    val code: String,
    val name: String,
    val leaderId: String,
    val members: List<RoomMember>,
    /** Selected game ID, or null if none selected. */
    val selectedGame: String? = null,
    /** Leader's "fill with bots up to the minimum" option (see SetBotFill). */
    val botFill: Boolean = false,
    /** Countdown deadline in epoch millis, or null if no countdown running. */
    val countdownDeadline: Long? = null,
    /** Match ID if a match is in progress, or null. */
    val matchInProgress: String? = null,
)

@Serializable
data class RoomMember(
    val userId: String,
    val displayName: String,
    val presence: Presence,
    val ready: Boolean,
)

enum class Presence {
    ONLINE,
    OFFLINE,
}

/** Tells clients a match is about to start. */
@Serializable
data class MatchStarting(
    val matchId: String,
)
