package com.kglabs28.yapnplay.appengine.protocol

import com.kglabs28.yapnplay.appengine.games.elements.board.protocol.LegalMove
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

// ══════════════════════════════════════════════════════════════════════════
// Match channel — client to server requests
// (Mechanic-specific requests like RollDice/MovePiece live in
// games/elements/board/protocol — this file holds only generic messages.)
// ══════════════════════════════════════════════════════════════════════════

/**
 * Ask for the current snapshot: on opening the match screen, after a
 * reconnect, or (a room member without a seat) to start watching.
 */
@Serializable
data class MatchRequestSnapshot(
    val matchId: String,
    val lastVersion: Long,
)

/** A game-specific action; [action] carries `"type"` plus its fields (e.g. `{"type":"BuildOnTile","tile":7}`). */
@Serializable
data class GameAction(
    val matchId: String,
    val action: JsonObject,
)

/** The seat's owner turns Autoplay on (the server plays their turns) or off. */
@Serializable
data class SetAutoplay(
    val matchId: String,
    val enabled: Boolean,
)

/** The user's mic switch at this table (shown on their seat or spectator chip). */
@Serializable
data class SetMic(
    val matchId: String,
    val micOn: Boolean,
)

// ══════════════════════════════════════════════════════════════════════════
// Match channel — server to client responses
// ══════════════════════════════════════════════════════════════════════════

/**
 * Full match state for one viewer, sent after every change. [events] are
 * what happened since this viewer's previous snapshot, in order, so the
 * table can animate them before showing the final state.
 */
@Serializable
data class MatchSnapshot(
    val matchId: String,
    val version: Long,
    /** Server time in epoch milliseconds. */
    val serverTime: Long,
    /** Who is viewing: player with seat, or spectator. */
    val viewer: ViewerInfo,
    val seats: List<SeatInfo>,
    /** Game-specific public state as JSON (`RoyalLudoState`, `CityBazaarState` wire shapes). */
    val publicState: JsonObject,
    /** Legal moves for the viewer when it is their turn; empty otherwise. */
    val legalMoves: List<LegalMove>,
    val turn: TurnInfo,
    val gameId: String = "",
    /** Room name, or "Quick Play". */
    val title: String = "",
    /** The room this match belongs to; null for Quick Play (no spectators, no handover). */
    val roomId: String? = null,
    /** When the match began (epoch millis). */
    val startedAt: Long = 0,
    val spectators: List<SpectatorInfo> = emptyList(),
    /** A seat changing hands; null when none. */
    val seatOffer: SeatOfferInfo? = null,
    /** Game-specific events (each with `"type"`), oldest first. */
    val events: List<JsonObject> = emptyList(),
    /** A round or time limit is reached: the keep-playing vote; null when none. */
    val extensionVote: ExtensionVoteInfo? = null,
    /** The coin pot (null for free games). */
    val pot: MatchPot? = null,
    /** The end-game vote while it runs (play is paused). */
    val endVote: EndVoteInfo? = null,
    /** The viewer is the room's leader or admin: they may offer a seat the bot plays to a spectator. */
    val canOfferSeats: Boolean = false,
    /** Players and spectators talking with their mic off (a grey glow on them). */
    val mutedTalkers: List<String> = emptyList(),
)

/** The user is talking with their mic off at this table: everyone sees a grey glow on them. */
@Serializable
data class SetMatchTalkingMuted(val matchId: String, val talking: Boolean)

/**
 * The end-game vote: [proposerSeat] asked to end; [voters] (humans still
 * in) decide by [endsAt]; the proposer counts as yes. More than half yes
 * ends the match (ranked like the time limit); a missing vote counts as no.
 * The result arrives as the event `{"type":"EndVoted","ended","yes","voters"}`.
 */
@Serializable
data class EndVoteInfo(
    val proposerSeat: Int,
    val endsAt: Long,
    val voters: List<Int>,
    val yes: List<Int> = emptyList(),
    val no: List<Int> = emptyList(),
)

/** MATCH `ProposeEndMatch`: ask everyone to end the match early. Rejected ILLEGAL_MOVE (not now) or RATE_LIMITED (cooldown). */
@Serializable
data class ProposeEndMatch(
    val matchId: String,
)

/** MATCH `CastEndVote`: a vote on ending the match early. Once per voter. */
@Serializable
data class CastEndVote(
    val matchId: String,
    val agree: Boolean,
)

/** [total] = entryCoins x (paid players + CPU seats); [split] = % of the pot by place (1st first). */
@Serializable
data class MatchPot(
    val entryCoins: Int,
    val total: Int,
    val split: List<Int>,
)

/**
 * The keep-playing vote: [voters] (humans still in) decide by [endsAt]
 * whether to play on; exactly one of [extraRounds] / [extraMinutes] is set,
 * by which limit was hit. Continue iff yes × 2 ≥ voters.
 */
@Serializable
data class ExtensionVoteInfo(
    val endsAt: Long,
    val voters: List<Int>,
    val yes: List<Int> = emptyList(),
    val no: List<Int> = emptyList(),
    val extraRounds: Int? = null,
    val extraMinutes: Int? = null,
)

/** MATCH `VoteExtension`: keep playing (true) or end (false). Reply Ack; Rejected ILLEGAL_MOVE / NOT_MEMBER. */
@Serializable
data class VoteExtension(
    val matchId: String,
    val keepPlaying: Boolean,
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
    /** True when a CPU fills this seat (Quick Play fill); controllerUserId is then a cpu id. */
    val cpu: Boolean = false,
    val displayName: String = "",
    val level: Int = 1,
    val micOn: Boolean = false,
    val presence: SeatPresence = SeatPresence.PLAYING,
    /** Preset avatar of whoever sits here (a CPU's robot avatar); "" = default icon. */
    val avatarId: String = "",
)

enum class Colour {
    RED,
    GREEN,
    YELLOW,
    BLUE,
    PURPLE,
    ORANGE,
}

/** At the table, away (the server plays their turns at once) or handed to the bot. */
enum class SeatPresence {
    PLAYING,
    AWAY,
    BOT,
}

enum class MatchSeatStatus {
    ACTIVE,
    REMOVED,
}

/** Someone watching a room match. */
@Serializable
data class SpectatorInfo(
    val userId: String,
    val displayName: String,
    val micOn: Boolean = false,
    /** Their avatar preset id ("" for none, or an older server). */
    val avatarId: String = "",
)

/**
 * A seat changing hands (see the contract's seat handover section).
 * [userId]/[name] are the person the stage is about: the absent owner,
 * the named spectator, or the deciding leader. [endsAt] epoch millis.
 */
@Serializable
data class SeatOfferInfo(
    val seat: Int,
    val leaverName: String,
    val stage: SeatOfferStageName,
    val endsAt: Long,
    val userId: String? = null,
    val name: String? = null,
    /** 1: the leaver's pick, 2: the leader's pick (TO_SPECTATOR only). */
    val round: Int? = null,
)

enum class SeatOfferStageName {
    CONFIRMING_ABSENCE,
    TO_SPECTATOR,
    LEADER_CHOOSING,
    OPEN_TO_ALL,
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
    /** Coins won from the pot: the place's share, for CPUs too (theirs is shown, never credited); 0 for free games. */
    val coinsWon: Int = 0,
    /** The entry this seat put in (0 for free games); a seat that won nothing shows it lost. */
    val stake: Int = 0,
    /** Coins for finishing the match (0 for CPUs, leavers, bot-held seats, short matches or past the daily cap). */
    val completionCoins: Int = 0,
    /** XP the server gave this seat (grows with match length; 0 for CPUs and leavers). */
    val xpGained: Int = 0,
    /** Someone whose seat another person took over (ranked last): their name and avatar; null otherwise. */
    val displayName: String? = null,
    val avatarId: String? = null,
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
    /** Absolute deadline in epoch milliseconds; null while play is on hold or finished. */
    val deadline: Long?,
)
