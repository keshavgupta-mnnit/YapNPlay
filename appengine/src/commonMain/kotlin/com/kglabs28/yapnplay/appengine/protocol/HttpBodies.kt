package com.kglabs28.yapnplay.appengine.protocol

import kotlinx.serialization.Serializable

// ══════════════════════════════════════════════════════════════════════════
// HTTP request/response bodies (REST API, not WebSocket)
// ══════════════════════════════════════════════════════════════════════════

/**
 * `POST /auth/guest` body: the account the app picked and saved before
 * asking, so a retry after a crash gets the same account back.
 */
@Serializable
data class CreateGuestBody(
    val userId: String,
    val secret: String,
    /** The phone, recorded with the account. */
    val deviceId: String? = null,
    /** With [deviceId]: true continues the account already made on that phone (the player chose it); false makes a new one. */
    val restore: Boolean = false,
)

/** `POST /auth/device`: is there an account made on this phone already? */
@Serializable
data class DeviceAccountBody(val deviceId: String)

/** The phone's account as offered back (never its secret). */
@Serializable
data class DeviceAccount(
    val found: Boolean,
    val displayName: String = "",
    val avatarId: String = "",
    val level: Int = 0,
)

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

/** `PATCH /me`: change the user's name or avatar (null = keep as is). */
@Serializable
data class UpdateProfileBody(
    val displayName: String? = null,
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
    /** Players by their 6-character code (friends or not); unknown codes come back in skipped. */
    val userCodes: List<String> = emptyList(),
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

/** `POST /coins/ad-reward` body: a unique id per ad view (a random UUID). */
@Serializable
data class AdRewardBody(
    val adId: String,
)

/** `POST /coins/ad-reward`: after a fully watched rewarded ad. [coins] is the new balance. */
@Serializable
data class CoinsReward(
    val coins: Int,
    val granted: Int,
    val adsLeftToday: Int,
    val dailyLimit: Int = 0,
    /** Epoch ms when the next ad can pay (≈ now + the gap, or tomorrow after the last one). */
    val nextAdAt: Long? = null,
    /** The day's reward ladder, ad by ad (e.g. 200, 300, 400, 500, 700). */
    val adRewards: List<Int> = emptyList(),
)

/** `GET /coins/leagues`: Quick Play leagues; [League.canEnter] is false when [coins] are short of the fee. */
@Serializable
data class Leagues(
    val coins: Int,
    val leagues: List<League>,
)

@Serializable
data class League(
    val entryCoins: Int,
    val name: String,
    val canEnter: Boolean,
)

/**
 * `GET /coins/ad-status`: the rewarded-ad limits. [nextAdAt] (epoch millis,
 * server time) is when the next ad can pay; null when one can pay now.
 */
@Serializable
data class AdRewardStatus(
    val coinsPerAd: Int,
    val dailyLimit: Int,
    val adsLeftToday: Int,
    val nextAdAt: Long? = null,
    val serverTime: Long,
    /** The day's reward ladder, ad by ad (e.g. 200, 300, 400, 500, 700); [coinsPerAd] is the next step. */
    val adRewards: List<Int> = emptyList(),
)

/**
 * `GET /matches/history`: the user's latest matches, newest first, each
 * with every seat as the result showed it (place, coins, XP).
 */
@Serializable
data class MatchHistoryResponse(val matches: List<MatchHistoryItem> = emptyList())

@Serializable
data class MatchHistoryItem(
    val matchId: String,
    val gameId: String,
    val roomMatch: Boolean = false,
    /** The room's name for a room match ("" for Quick Play or a deleted room). */
    val roomName: String = "",
    val startedAt: Long,
    val endedAt: Long,
    /** The user's own seat. */
    val mySeat: Int = 0,
    val seats: List<MatchHistorySeat> = emptyList(),
)

/** One seat of a past match: [userId] null for a CPU; coins won from the pot, for finishing, and staked. */
@Serializable
data class MatchHistorySeat(
    val seat: Int,
    val userId: String? = null,
    val name: String = "",
    val avatarId: String = "",
    val place: Int,
    val coinsWon: Int = 0,
    val completionCoins: Int = 0,
    val stake: Int = 0,
    val xp: Int = 0,
    /** They left before the match ended. */
    val left: Boolean = false,
)

/** One member's record in a room (`GET /rooms/{roomId}/leaderboard`). */
@Serializable
data class RoomStanding(
    val userId: String,
    val displayName: String = "",
    val avatarId: String = "",
    val played: Int = 0,
    val wins: Int = 0,
    val coins: Int = 0,
)

/** `GET /rooms/{roomId}/leaderboard`: every member with their record in the room. */
@Serializable
data class RoomLeaderboard(
    val standings: List<RoomStanding> = emptyList(),
)

/** `GET /rooms/{roomId}/invites`: users with an invite to the room they haven't answered yet. */
@Serializable
data class RoomPendingInvites(
    val userIds: List<String> = emptyList(),
)
