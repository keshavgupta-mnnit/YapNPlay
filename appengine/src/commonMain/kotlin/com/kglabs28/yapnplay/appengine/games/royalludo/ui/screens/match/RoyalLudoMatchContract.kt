package com.kglabs28.yapnplay.appengine.games.royalludo.ui.screens.match

import com.kglabs28.yapnplay.appengine.features.seathandover.presentation.SeatHandoverAction
import com.kglabs28.yapnplay.appengine.features.seathandover.presentation.SeatHandoverModal
import com.kglabs28.yapnplay.appengine.ui.components.moderation.EndVoteUi
import com.kglabs28.yapnplay.appengine.ui.components.leaderboard.LeaderboardUi
import com.kglabs28.yapnplay.appengine.ui.components.table.TablePlayerCardUi
import com.kglabs28.yapnplay.appengine.ui.components.table.PlayerFriendActionUi
import com.kglabs28.yapnplay.appengine.core.mvi.UiEffect
import com.kglabs28.yapnplay.appengine.core.mvi.UiIntent
import com.kglabs28.yapnplay.appengine.core.mvi.UiState
import com.kglabs28.yapnplay.appengine.features.seathandover.presentation.SeatPickOptionUi
import com.kglabs28.yapnplay.appengine.games.royalludo.domain.LudoColour
import com.kglabs28.yapnplay.appengine.ui.components.table.TableAvatarUi
import com.kglabs28.yapnplay.appengine.ui.components.moderation.ReportPersonUi

/**
 * Royal Ludo table. The board is turned so the user's base is
 * bottom-left. The user's card sits on the left; the other players'
 * cards stack on the right in turn order (clockwise from the user).
 * Pieces move one event at a time so hops, captures and undone turns can
 * animate. Everything is formatted here, ready to draw.
 */
data class RoyalLudoMatchState(
    val loaded: Boolean = false,
    val title: String = "",
    val subtitle: String = "",
    /** Quarter turns that bring the user's base to the bottom-left. */
    val rotation: Int = 0,
    val pieces: List<LudoPieceUi> = emptyList(),
    val myCard: LudoPlayerCardUi? = null,
    /** The other players, clockwise from the user. */
    val opponents: List<LudoPlayerCardUi> = emptyList(),
    /** Finishing place shown inside each finished player's base, by colour. */
    val basePlaces: Map<LudoColour, String> = emptyMap(),
    /** Top bar: the people watching (players are on the cards). */
    val audience: List<TableAvatarUi> = emptyList(),
    val spectatorCount: String = "",
    /** Time since the match began, "m:ss", for the top bar. */
    val elapsed: String = "",
    val dice: LudoDiceUi = LudoDiceUi(),
    val canRoll: Boolean = false,
    /** "Move 4" while the user must pick a piece to move; null otherwise. */
    val moveHint: String? = null,
    /** "Paused · Riya's seat offered to Karan · 24s" while a seat changes hands. */
    val seatBanner: String? = null,
    /** A room match: spectators, their strip in the top bar and seat handover. */
    val roomMatch: Boolean = false,
    /** The user watches (no seat): every player's card, two on each side by where their house is. */
    val spectating: Boolean = false,
    val leftCards: List<LudoPlayerCardUi> = emptyList(),
    val rightCards: List<LudoPlayerCardUi> = emptyList(),
    val autoplay: Boolean = false,
    val micOn: Boolean = false,
    /** The user's speaker: off, they hear nobody (only on their device). */
    val speakerOn: Boolean = true,
    val modal: LudoMatchModal? = null,
    /** The match is over: the full-screen leaderboard. */
    val leaderboard: LeaderboardUi? = null,
    /** Long-press add / remove friend, while its confirm is open. */
    val friendAction: PlayerFriendActionUi? = null,
) : UiState

/**
 * How a piece reaches its new spot: a hop per square, a glide out of or
 * back to base (a six, capture, undone turn), or a small flat shift (making room on a
 * shared square).
 */
enum class PieceMotion { HOP, SLIDE, SHIFT }

/** One piece on the board ([progress] as in the engine). */
data class LudoPieceUi(
    val id: String,
    val seat: Int,
    val piece: Int,
    val colour: LudoColour,
    val progress: Int,
    val motion: PieceMotion,
    /** The user may move this piece now. */
    val selectable: Boolean,
)

enum class BoardCorner { TOP_LEFT, TOP_RIGHT, BOTTOM_RIGHT, BOTTOM_LEFT }

/** A player's card. */
data class LudoPlayerCardUi(
    override val seat: Int,
    val userId: String,
    override val name: String,
    override val avatarId: String = "",
    /** Level number, on the avatar's badge. */
    override val level: String,
    val colour: LudoColour,
    val corner: BoardCorner,
    override val isMe: Boolean,
    override val isTurn: Boolean,
    override val talking: Boolean,
    override val talkingMuted: Boolean = false,
    /** 1 → 0 while it is this player's turn. */
    override val timerFraction: Float,
    val left: Boolean,
    /** "1st", "2nd"… once they have finished; null while playing. */
    val placeLabel: String?,
    /** The user muted this player for themselves only. */
    override val mutedForMe: Boolean,
    /** False for a CPU: no voice wave and no mute button (it never talks). */
    override val canSpeak: Boolean = true,
    /** Their mic is on (the mute button is live only then). */
    override val micOn: Boolean = false,
    /** "Offered" while their seat changes hands, "Away" or "Bot" while the server plays for them; null otherwise. */
    val statusLabel: String? = null,
) : TablePlayerCardUi


/**
 * The die: [rollId] changes on every roll so the screen animates it
 * once; [colour] is the player whose turn it is; [visible] is false from
 * the end of a roll that needs a move until that move is done, so the
 * whole board shows.
 */
data class LudoDiceUi(val value: Int = 1, val rollId: Int = 0, val colour: LudoColour? = null, val visible: Boolean = true)

sealed interface LudoMatchModal {
    /** "End game" was tapped: asked first, the vote starts only on End game. */
    data object ConfirmEnd : LudoMatchModal
    /** Leave? In a room match, also who gets the seat ([seatOptions]: the bot first, then spectators). */
    data class LeaveConfirm(val body: String, val seatOptions: List<SeatPickOptionUi>, val selectedIndex: Int, val countdown: String = "") : LudoMatchModal
    data class Result(val rows: List<LudoResultRowUi>) : LudoMatchModal
    /** Mic permission was refused: explain and offer Settings. */
    data object MicPermissionNeeded : LudoMatchModal
    /** Match settings: sound effects, music, report a player; [canEndGame] adds "End game (vote)". */
    data class Settings(val effectsOn: Boolean, val musicOn: Boolean, val canEndGame: Boolean = false) : LudoMatchModal
    /** Someone asked to end the game: the table votes (play is paused). */
    data class EndVote(val vote: EndVoteUi) : LudoMatchModal
    /** Pick who to report for voice chat; [warning] explains a false report is held against the reporter. */
    data class Report(val people: List<ReportPersonUi>, val selectedUserId: String?, val warning: String, val error: String?) : LudoMatchModal
    /** "Did they do it?" for someone else's report; closes by itself when [secondsLeft] runs out. */
    data class Vote(val reportId: String, val title: String, val body: String, val secondsLeft: String) : LudoMatchModal
    /** How the user's report ended. */
    data class ReportOutcome(val title: String, val body: String) : LudoMatchModal
    /** An incident was recorded against the user: points lost, play blocked; an ad lifts it. */
    /** [adOffered]: Watch Ad shows (ads on and one ready). */
    data class Penalty(val title: String, val body: String, val watchingAd: Boolean, val adOffered: Boolean) : LudoMatchModal
    /** Shown to a spectator: take the seat someone left (button reads [takeLabel] with the time left). */
    /** A seat dialog, the same in every game. */
    data class Seat(val modal: SeatHandoverModal) : LudoMatchModal
}

data class LudoResultRowUi(val place: String, val name: String, val colour: LudoColour)


sealed interface RoyalLudoMatchIntent : UiIntent {
    data class Load(val matchId: String) : RoyalLudoMatchIntent
    data object Roll : RoyalLudoMatchIntent
    data class PieceClicked(val seat: Int, val piece: Int) : RoyalLudoMatchIntent
    data object ToggleAutoplay : RoyalLudoMatchIntent
    /** [permissionGranted]: whether the OS mic permission is granted right now. */
    data class ToggleMic(val permissionGranted: Boolean) : RoyalLudoMatchIntent
    data class MicPermissionResult(val granted: Boolean) : RoyalLudoMatchIntent
    data object OpenAppSettings : RoyalLudoMatchIntent
    /** Mute or unmute this player for the user only. */
    data class ToggleHear(val seat: Int) : RoyalLudoMatchIntent
    /** The user's speaker on or off (for them only). */
    data object ToggleSpeaker : RoyalLudoMatchIntent
    /** Mute or unmute a spectator for the user only. */
    data class ToggleHearSpectator(val userId: String) : RoyalLudoMatchIntent
    /** Long press on a player card: add or remove them as a friend. */
    data class PlayerLongPressed(val seat: Int) : RoyalLudoMatchIntent
    data object ConfirmFriendAction : RoyalLudoMatchIntent
    data object DismissFriendAction : RoyalLudoMatchIntent
    data object ExitClicked : RoyalLudoMatchIntent
    data object ShareResult : RoyalLudoMatchIntent
    data object ConfirmLeave : RoyalLudoMatchIntent
    data object DismissModal : RoyalLudoMatchIntent
    data object OpenSettings : RoyalLudoMatchIntent
    /** "End game (vote)" in the settings. */
    data object ProposeEnd : RoyalLudoMatchIntent
    /** "END THE GAME?" answered End game: the vote starts. */
    data object ConfirmEnd : RoyalLudoMatchIntent
    data class VoteEnd(val agree: Boolean) : RoyalLudoMatchIntent
    data object ToggleEffects : RoyalLudoMatchIntent
    data object ToggleMusic : RoyalLudoMatchIntent
    data object OpenReport : RoyalLudoMatchIntent
    data class SelectReportPerson(val userId: String) : RoyalLudoMatchIntent
    data object SubmitReport : RoyalLudoMatchIntent
    /** Answer to someone else's report. */
    data class Vote(val agree: Boolean) : RoyalLudoMatchIntent
    data object WatchAd : RoyalLudoMatchIntent
    /** The table came to the front, or went away (another screen, or the app to the background). */
    data class ScreenVisible(val visible: Boolean) : RoyalLudoMatchIntent
    data object Done : RoyalLudoMatchIntent
    /** An answer in a seat dialog, or a long press on a bot's seat. */
    data class Seat(val action: SeatHandoverAction) : RoyalLudoMatchIntent
    /** Picked who plays on in the exit dialog. */
    data class PickSeatTaker(val index: Int) : RoyalLudoMatchIntent
}

sealed interface RoyalLudoMatchEffect : UiEffect {
    data object Close : RoyalLudoMatchEffect
    data object RequestMicPermission : RoyalLudoMatchEffect
    data object OpenAppSettings : RoyalLudoMatchEffect
    data class ShareText(val text: String) : RoyalLudoMatchEffect
}
