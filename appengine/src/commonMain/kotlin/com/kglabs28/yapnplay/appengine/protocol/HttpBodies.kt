package com.kglabs28.yapnplay.appengine.protocol

import kotlinx.serialization.Serializable

// ══════════════════════════════════════════════════════════════════════════
// HTTP request/response bodies (REST API, not WebSocket)
// ══════════════════════════════════════════════════════════════════════════

/** Response after creating a new guest account. */
@Serializable
data class GuestCreated(
    val userId: String,
    /** Secret token used as the WebSocket bearer credential. */
    val secret: String,
)

/** Response with the current user's identity. */
@Serializable
data class Me(
    val userId: String,
    val displayName: String,
    /** Server-computed from all match results (random and room matches). */
    val xp: Int = 0,
    /** Derived from [xp] by the server. */
    val level: Int = 1,
    /** Provisional: economy not designed yet. */
    val coins: Int = 0,
    /** Provisional: economy not designed yet. */
    val gems: Int = 0,
)

/** Provisional (post-POC friends): the user's friends, online first. */
@Serializable
data class Friends(
    val friends: List<FriendSummary>,
)

@Serializable
data class FriendSummary(
    val userId: String,
    val displayName: String,
    val online: Boolean,
)

/** How many users are connected right now (Game Zone "1248 Online"). */
@Serializable
data class OnlineStats(
    val online: Int,
)

/** Request to update the display name. */
@Serializable
data class UpdateName(
    val displayName: String,
)

/** Response with the list of rooms the user belongs to. */
@Serializable
data class MyRooms(
    val rooms: List<RoomSummary>,
)

@Serializable
data class RoomSummary(
    val roomId: String,
    val code: String,
    val name: String,
    val memberCount: Int,
    /** Members present right now — shown as "N live". */
    val onlineCount: Int,
    /** Whether the requesting user currently leads this room. */
    val isLeader: Boolean = false,
    /** Room badge: group, dice, family or gamepad (unknown → group). */
    val icon: String? = null,
)

/** Response with a voice provider connection token. */
@Serializable
data class VoiceToken(
    /** WebSocket URL for the voice provider. */
    val url: String,
    /** Access token for the voice provider. */
    val token: String,
    /** Expiry time in epoch milliseconds. */
    val expiresAt: Long,
)
