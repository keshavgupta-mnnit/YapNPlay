package com.kglabs28.yapnplay.appengine.games.doodleking.ui.screens.match

import com.kglabs28.yapnplay.appengine.ui.components.seat.KeepPlayingUi
import com.kglabs28.yapnplay.appengine.ui.components.moderation.EndVoteUi
import com.kglabs28.yapnplay.appengine.features.seathandover.presentation.SeatHandoverModal
import com.kglabs28.yapnplay.appengine.features.seathandover.presentation.SeatHandoverAction
import com.kglabs28.yapnplay.appengine.features.seathandover.presentation.LeaveDialogUi
import com.kglabs28.yapnplay.appengine.core.mvi.UiEffect
import com.kglabs28.yapnplay.appengine.core.mvi.UiIntent
import com.kglabs28.yapnplay.appengine.core.mvi.UiState
import com.kglabs28.yapnplay.appengine.ui.components.leaderboard.LeaderboardUi
import com.kglabs28.yapnplay.appengine.ui.components.table.TableAvatarUi
import com.kglabs28.yapnplay.appengine.ui.components.table.TablePlayerCardUi

/** What the bottom bar's middle is for the user right now. */
enum class DoodleBarMode {
    /** Their turn to draw: the tools, and the word with Pass. */
    DRAW,
    /** Guessing: the hint and the guess box. */
    GUESS,
    /** Already guessed: the box is chat for those who guessed. */
    CHAT,
    /** Nothing to type (between turns, or the drawer picking). */
    WAIT,
}

data class DoodleKingMatchState(
    val loaded: Boolean = false,
    val title: String = "",
    val subtitle: String = "",
    val audience: List<TableAvatarUi> = emptyList(),
    val showAudience: Boolean = false,
    val spectatorCount: String = "",
    val elapsed: String = "",
    /** Over the canvas: whose turn and what they're doing. */
    val status: String = "",
    /** The phase's time left ("0:45"), and its last seconds. */
    val timeLeft: String = "",
    val timeUrgent: Boolean = false,
    /** The same object all match long: the canvas redraws itself from it. */
    val board: DoodleBoard = DoodleBoard(),
    val barMode: DoodleBarMode = DoodleBarMode.WAIT,
    /** The pen: colour index, size (0–2), eraser. */
    val inkColour: Int = 0,
    val inkSize: Int = 1,
    val eraser: Boolean = false,
    /** The drawer's word; empty for everyone else. */
    val word: String = "",
    /** The word as guessers see it (`_` per hidden letter). */
    val hint: String = "",
    val leftCards: List<DoodleCardUi> = emptyList(),
    val myCard: DoodleCardUi? = null,
    val micOn: Boolean = false,
    /** The user's mic is closed for their turn to draw (they can't open it). */
    val micLocked: Boolean = false,
    val speakerOn: Boolean = true,
    val spectating: Boolean = false,
    val feed: List<DoodleFeedRowUi> = emptyList(),
    val ranks: List<DoodleRankRowUi> = emptyList(),
    /** The drawer's 3 words while picking. */
    val pick: DoodlePickUi? = null,
    /** The word and everyone's points, between turns. */
    val reveal: DoodleRevealUi? = null,
    val modal: DoodleModal? = null,
    /** The final board once the match is over. */
    val leaderboard: LeaderboardUi? = null,
) : UiState

/** A player's card (left column, and the user's own in the bar). */
data class DoodleCardUi(
    override val seat: Int,
    override val name: String,
    override val avatarId: String,
    override val level: String,
    override val isMe: Boolean,
    override val isTurn: Boolean,
    override val talking: Boolean,
    override val talkingMuted: Boolean,
    override val turnEndsAtMillis: Long?,
    override val turnTotalMs: Long,
    override val mutedForMe: Boolean,
    override val canSpeak: Boolean,
    override val micOn: Boolean,
    /** "120 pts". */
    val score: String,
    /** DRAWING, GUESSED, Away; null: none. */
    val tag: String?,
    val left: Boolean,
) : TablePlayerCardUi

/** One feed line: [name] (in the seat's colour) then [text]. */
data class DoodleFeedRowUi(val key: Int, val seat: Int, val name: String, val text: String, val tone: DoodleFeedTone)

enum class DoodleFeedTone { GUESS, CLOSE, CORRECT, CHAT, NOTE }

data class DoodleRankRowUi(val seat: Int, val place: String, val name: String, val score: String, val isMe: Boolean)

data class DoodlePickUi(val words: List<String>, val seconds: String)

data class DoodleRevealUi(val word: String, val rows: List<DoodleRevealRowUi>)

data class DoodleRevealRowUi(val name: String, val points: String, val gained: Boolean)

/** The table's dialogs; the leave, seat and vote ones are every table's. */
sealed interface DoodleModal {
    data class Settings(val effectsOn: Boolean, val musicOn: Boolean, val canProposeEnd: Boolean) : DoodleModal
    data class Leave(val dialog: LeaveDialogUi) : DoodleModal
    data object MicPermissionNeeded : DoodleModal
    data object ConfirmEnd : DoodleModal
    data class EndVote(val vote: EndVoteUi) : DoodleModal
    data class KeepPlaying(val vote: KeepPlayingUi) : DoodleModal
    data class Seat(val modal: SeatHandoverModal) : DoodleModal
}

sealed interface DoodleKingMatchIntent : UiIntent {
    data class Load(val matchId: String) : DoodleKingMatchIntent
    data class ScreenVisible(val visible: Boolean) : DoodleKingMatchIntent
    data class ToggleMic(val permissionGranted: Boolean) : DoodleKingMatchIntent
    data class MicPermissionResult(val granted: Boolean) : DoodleKingMatchIntent
    data object OpenAppSettings : DoodleKingMatchIntent
    data object ToggleSpeaker : DoodleKingMatchIntent
    data class ToggleHear(val seat: Int) : DoodleKingMatchIntent
    data class ToggleHearSpectator(val userId: String) : DoodleKingMatchIntent
    data class PickWord(val index: Int) : DoodleKingMatchIntent
    data class SendGuess(val text: String) : DoodleKingMatchIntent
    data object Pass : DoodleKingMatchIntent
    data class SelectInk(val colour: Int) : DoodleKingMatchIntent
    /** The brush: picks it (from the eraser), or steps its size when it's already in hand. */
    data object SelectBrush : DoodleKingMatchIntent
    data object SelectEraser : DoodleKingMatchIntent
    data object Undo : DoodleKingMatchIntent
    data object Clear : DoodleKingMatchIntent
    data object OpenSettings : DoodleKingMatchIntent
    data object ToggleEffects : DoodleKingMatchIntent
    data object ToggleMusic : DoodleKingMatchIntent
    data object ExitClicked : DoodleKingMatchIntent
    data object ConfirmLeave : DoodleKingMatchIntent
    /** Who plays on for the user when they leave (a CPU or a spectator). */
    data class PickSeatTaker(val index: Int) : DoodleKingMatchIntent
    /** A seat dialog's answer, or opening a bot's seat offer: every table's handover. */
    data class Seat(val action: SeatHandoverAction) : DoodleKingMatchIntent
    data class PlayerLongPressed(val seat: Int) : DoodleKingMatchIntent
    data object ProposeEnd : DoodleKingMatchIntent
    data object ConfirmEnd : DoodleKingMatchIntent
    data class VoteEnd(val agree: Boolean) : DoodleKingMatchIntent
    data class VoteKeepPlaying(val keepPlaying: Boolean) : DoodleKingMatchIntent
    data object DismissModal : DoodleKingMatchIntent
    /** Leaves the finished match's board. */
    data object Done : DoodleKingMatchIntent
}

sealed interface DoodleKingMatchEffect : UiEffect {
    data object Close : DoodleKingMatchEffect
    data object RequestMicPermission : DoodleKingMatchEffect
    data object OpenAppSettings : DoodleKingMatchEffect
}
