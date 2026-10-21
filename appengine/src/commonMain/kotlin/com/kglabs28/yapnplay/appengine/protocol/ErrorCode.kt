package com.kglabs28.yapnplay.appengine.protocol

/**
 * Typed error codes sent in Rejected messages.
 * The app maps these to user-facing strings; never show raw codes to users.
 */
enum class ErrorCode {
    NOT_AUTHENTICATED,
    NOT_FOUND,
    NOT_MEMBER,
    NOT_LEADER,
    NOT_ADMIN,
    ROOM_FULL,
    INVALID_CODE,
    ALREADY_IN_ROOM,
    NOT_ENOUGH_READY,
    STALE_VERSION,
    NOT_YOUR_TURN,
    ILLEGAL_MOVE,
    MATCH_ENDED,
    RATE_LIMITED,
    BAD_REQUEST,
    ROOM_LIMIT_REACHED,
    ALREADY_IN_QUEUE,
    GAME_UNAVAILABLE,
    /** A penalty blocks new matches until PenaltyIssued.blockedUntil. */
    BLOCKED,
    /** Someone else took the open seat first. */
    SEAT_TAKEN,
    /** Voice is not set up on this server. */
    VOICE_UNAVAILABLE,
    /** Only friends can be invited to a room. */
    NOT_FRIEND,
    /** The match hasn't started: it's waiting for every player to open the table. */
    MATCH_ON_HOLD,
    /** The server can't check Play Games sign-ins (not configured). */
    PLAY_GAMES_UNAVAILABLE,
    /** CreateRoom: the creator already has a group with exactly these members; the message is its name. */
    GROUP_EXISTS,
    /** JoinQueue: the user can't pay the league's entry fee. */
    NOT_ENOUGH_COINS,
}
