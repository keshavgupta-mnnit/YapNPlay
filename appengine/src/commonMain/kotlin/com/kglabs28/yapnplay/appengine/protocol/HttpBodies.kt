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
    /** The server's coin balance (new players start at 1000). */
    val coins: Int = 0,
    /** XP where this level starts / where the next one starts (the level bar). */
    val levelXp: Int = 0,
    val nextLevelXp: Int = 0,
    /** Coins added by this call (the daily bonus: first /me of the day); 0 otherwise. */
    val dailyBonus: Int = 0,
    /** Provisional: economy not designed yet. */
    val gems: Int = 0,
    /** Shown as "@username"; anyone may pick any (not unique). */
    val username: String = "",
    /** The user's 6-character ID that friends type to add them (unique). */
    val userCode: String = "",
    /** One of the app's preset avatars ("" = default). */
    val avatarId: String = "",
    val gamesPlayed: Int = 0,
    val gamesWon: Int = 0,
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
    val username: String = "",
    val userCode: String = "",
    val avatarId: String = "",
)

/**
 * What a room looks like before joining (`GET /rooms/preview/{code}`),
 * for the "Join this room?" dialog from an invite link or a typed code.
 */
@Serializable
data class RoomPreview(
    val roomId: String,
    val code: String,
    val name: String,
    val description: String? = null,
    val memberCount: Int,
    val onlineCount: Int,
    /** True when the caller (from the connection) is already a member. */
    val isMember: Boolean = false,
)

/** Registers this device for push (`POST /devices`); token is the FCM token. */
@Serializable
data class RegisterDevice(
    val token: String,
    val platform: String,
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

/** Answer to `JoinRoom(code)` from someone who is not a member yet. */
@Serializable
enum class JoinStatus { JOINED, PENDING_APPROVAL }

/**
 * `JoinRoom(code)` result: [JoinStatus.JOINED] with [room] for a member;
 * otherwise [JoinStatus.PENDING_APPROVAL] — the request sits in the
 * admin's Waiting List and the user is notified once approved.
 */
@Serializable
data class JoinRoomResult(
    val status: JoinStatus,
    val roomName: String,
    val room: RoomSummary? = null,
)

/** `PATCH /me`: change the user's name, username or avatar (null = keep as is). */
@Serializable
data class UpdateProfileBody(
    val displayName: String? = null,
    val username: String? = null,
    val avatarId: String? = null,
)

/** `GET /friends/requests`: friend requests other people sent the user, newest first. */
@Serializable
data class FriendRequests(
    val requests: List<FriendRequestSummary>,
)

@Serializable
data class FriendRequestSummary(
    val requestId: String,
    val from: FriendSummary,
)

/** `POST /friends/requests`: ask the person whose ID is [userCode] to be friends. */
@Serializable
data class SendFriendRequestBody(
    val userCode: String = "",
    /** Instead of the ID: someone the user can see by id (a room member card). */
    val userId: String? = null,
)

@Serializable
data class SendFriendRequestReply(
    val outcome: FriendRequestOutcomeCode,
)

/**
 * What became of a friend request: sent; nobody has that ID; it's the
 * user's own ID; already friends; already asked; the user blocked them;
 * or they had already asked the user, so now they're friends.
 */
@Serializable
enum class FriendRequestOutcomeCode { SENT, NOT_FOUND, SELF, ALREADY_FRIENDS, ALREADY_REQUESTED, BLOCKED, ACCEPTED }

/** `POST /blocks`: block a person (also ends any friendship and pending request). */
@Serializable
data class BlockUserBody(
    val userId: String,
)

/** `GET /blocks`: people the user blocked, newest first. Unblock with `DELETE /blocks/{userId}`. */
@Serializable
data class BlockedUsers(
    val users: List<BlockedUserSummary>,
)

@Serializable
data class BlockedUserSummary(
    val userId: String,
    val displayName: String,
    val username: String = "",
    val avatarId: String = "",
    /** When the user blocked them (epoch millis). */
    val blockedAt: Long,
)

/**
 * `GET /matches/active`: the unfinished match the caller still holds a seat
 * in (they closed the app or lost the connection), so the app can offer to
 * rejoin. 204 No Content when there is none (or they chose Leave).
 */
@Serializable
data class ActiveMatch(
    val matchId: String,
    val gameId: String,
    /** Room name, or "Quick Play". */
    val title: String,
    val roomId: String? = null,
)

/** `POST /rooms/{roomId}/invites`: invite friends into a room the caller belongs to. */
@Serializable
data class InviteToRoomBody(
    val userIds: List<String>,
)

/** Who got an invite, and who was skipped (not a friend, blocked, already a member or invited, room full). */
@Serializable
data class InviteToRoomReply(
    val invited: List<String>,
    val skipped: List<String> = emptyList(),
)

/** `GET /rooms/invites`: room invites waiting for the user, newest first. */
@Serializable
data class RoomInvites(
    val invites: List<RoomInviteSummary>,
)

@Serializable
data class RoomInviteSummary(
    val inviteId: String,
    val roomId: String,
    val roomName: String,
    /** Room badge key, same as RoomSummary.icon. */
    val roomIcon: String = "group",
    val memberCount: Int,
    val invitedBy: FriendSummary,
    /** Epoch millis. */
    val invitedAt: Long,
)

/** `POST /auth/play-games`: the one-time code from Google Play Games (sent with the current guest's bearer). */
@Serializable
data class PlayGamesSignInBody(
    val serverAuthCode: String,
)

/**
 * The account to use from now on. [restored]: this Play Games player was
 * already linked to another account (a new phone or a reinstall), which is
 * returned instead of the current guest.
 */
@Serializable
data class PlayGamesSignedIn(
    val userId: String,
    val secret: String,
    val restored: Boolean = false,
)
