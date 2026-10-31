package com.kglabs28.yapnplay.appengine.ui.screens.mono

import androidx.compose.ui.graphics.Color
import com.kglabs28.yapnplay.appengine.features.seathandover.presentation.SeatHandoverAction
import com.kglabs28.yapnplay.appengine.features.seathandover.presentation.SeatHandoverModal
import com.kglabs28.yapnplay.appengine.ui.components.table.TableFeedLineUi
import com.kglabs28.yapnplay.appengine.ui.components.moderation.TableSheetUi
import com.kglabs28.yapnplay.appengine.features.seathandover.presentation.LeaveDialogUi
import com.kglabs28.yapnplay.appengine.core.mvi.UiEffect
import com.kglabs28.yapnplay.appengine.core.mvi.UiIntent
import com.kglabs28.yapnplay.appengine.core.mvi.UiState
import com.kglabs28.yapnplay.appengine.games.mono.domain.MonoMatch
import com.kglabs28.yapnplay.appengine.games.mono.identity.MonoConstants
import com.kglabs28.yapnplay.appengine.ui.components.leaderboard.LeaderboardUi
import com.kglabs28.yapnplay.appengine.ui.components.table.MonoFlight
import com.kglabs28.yapnplay.appengine.ui.components.table.TableAvatarUi

/**
 * Standard MONO table. The table felt sits in the middle: the draw and
 * discard piles on it, the players' pods around it, the user's hand
 * along the bottom. Everything is resolved here so the screen only draws.
 */
data class MatchMonoState(
    val loaded: Boolean = false,
    val error: String? = null,
    val match: MonoMatch? = null,
    val myTurn: Boolean = false,
    val selectedCardIndex: Int? = null,
    /**
     * Hand indexes the table will accept right now. Everything else is
     * drawn pushed back, so the player never taps a card that would be
     * refused. Empty while it is not their turn.
     */
    val playableIndexes: Set<Int> = emptySet(),
    /** True while the table waits for the user to pick the next colour. */
    val awaitingColorChoice: Boolean = false,
    /** A draw penalty on the table that the user may stack onto or take. */
    val activeStackPenalty: Int = 0,
    /** A stack is on the user: stack a draw card onto it, or take the penalty. */
    val canStack: Boolean = false,
    /** The user may call MONO now (their turn on two cards, or just down to one without a call). */
    val canCallMono: Boolean = false,
    /** Someone just went down to one card and the user may press CAUGHT on them. */
    val canCatch: Boolean = false,
    /** The user drew a playable card and may decline to play it. */
    val canPass: Boolean = false,
    /** "1st", "2nd"… as seats finish; seat → label. */
    val places: Map<Int, String> = emptyMap(),
    /** When the current turn ends (this device's clock); null: not running. */
    val turnEndsAtMillis: Long? = null,
    val turnTotalMs: Long = 0,
    /** The turn countdown, "0:15". */
    val turnClock: String = "",
    /** The colour in force, which lights the table's ring. */
    val topColour: MonoConstants.Color? = null,
    /** True when the turn runs counter-clockwise. */
    val reversed: Boolean = false,
    val title: String = "",
    val elapsed: String = "",
    val spectatorCount: Int = 0,
    /** Spectators and absent players, for the top bar strip. */
    val audience: List<TableAvatarUi> = emptyList(),
    /** Set once someone has emptied their hand. */
    val winnerSeat: Int? = null,
    val finished: Boolean = false,
    val micOn: Boolean = false,
    /** The user's speaker: off, they hear nobody (only on their device). */
    val speakerOn: Boolean = true,
    /** People the user muted for themselves. */
    val mutedForMe: Set<String> = emptySet(),
    val autoplay: Boolean = false,
    /** User ids talking right now (the blue-pink glow behind their seat). */
    val talking: Set<String> = emptySet(),
    /** User ids talking with their mic off (a grey glow behind their seat). */
    val mutedTalkers: Set<String> = emptySet(),
    /** The keep-playing vote, once two or more have finished. */
    val vote: MonoVoteUi? = null,
    /** Settings (sound, music, report), a report to vote on, or its outcome; null when none is open. */
    val sheet: TableSheetUi? = null,
    /** A seat dialog the user owes (the same in every game); null when none. */
    val seatModal: SeatHandoverModal? = null,
    /** Exit was tapped: "Leave the match?" waits for an answer (and who takes the seat). */
    val leaveDialog: LeaveDialogUi? = null,
    /** "End game" was tapped in the settings: "END THE GAME?" waits for an answer before the vote starts. */
    val confirmEnd: Boolean = false,
    /** The final board, once the match is over (after the winner's banner has shown). */
    val leaderboard: LeaderboardUi? = null,
    /** "🏆 Riya finishes 1st!" for a few seconds after someone empties their hand. */
    val finishBanner: String? = null,
    /** [finishBanner] tells of a colour being named: shown in that colour. */
    val bannerIsColour: Boolean = false,
    /** A word of [finishBanner] shown in its colour (the Roulette's colour); null: none. */
    val bannerWord: Pair<String, Color>? = null,
    /** A MONO! penalty that concerns the user (caught, missed, wrong catch), as a snackbar for a moment. */
    val penaltySnack: TableFeedLineUi? = null,
) : UiState {
    val hand get() = match?.me?.hand.orEmpty()
    /** The hand grouped by colour (numbers, then that colour's power cards), wilds last (indexes into [hand]). */
    val handOrder: List<Int>
        get() = MonoHandOrder.mono(hand)
    /** Everyone else, clockwise from the user's seat (the order turns go round the table). */

    /** Everyone else still in the game, clockwise from the user (a player who left takes no seat). */
    /** The user has finished: no more MONO!, CAUGHT or Draw for them. */
    val outOfPlay: Boolean get() = match?.me?.seat?.let { it in match.finishedSeats } == true
    val opponents get() = MonoUiMapper.clockwise(match?.players.orEmpty().filter { !it.isMe && !it.removed }, match?.me?.seat) { it.seat }
}

sealed interface MatchMonoIntent : UiIntent {
    data class Load(val matchId: String) : MatchMonoIntent
    /** Taps a card. A legal card plays at once; an illegal one does nothing. */
    data class TapCard(val index: Int) : MatchMonoIntent
    data object DrawCard : MatchMonoIntent
    data class ChooseColor(val color: MonoConstants.Color) : MatchMonoIntent
    data object CallMono : MatchMonoIntent
    /** Call out the seat that just went down to one card without calling MONO. */
    data object Caught : MatchMonoIntent
    /** The keep-playing vote: true keeps the match going. */
    data class VoteContinue(val keepPlaying: Boolean) : MatchMonoIntent
    /** Take the penalty on the table instead of stacking onto it. */
    data object AcceptStack : MatchMonoIntent
    /** End the turn after drawing a playable card and declining to play it. */
    data object Pass : MatchMonoIntent
    data class SetAutoplay(val enabled: Boolean) : MatchMonoIntent
    /** The mic button: the user's mic switch, the same in every game (turning it on asks for the mic first). */
    data object ToggleMic : MatchMonoIntent
    /** The phone's answer to the mic permission. */
    data class MicPermissionResult(val granted: Boolean) : MatchMonoIntent
    /** The user's speaker on or off (for them only). */
    data object ToggleSpeaker : MatchMonoIntent
    /** Mute or unmute a player (by seat) for the user only. */
    data class ToggleHear(val seat: Int) : MatchMonoIntent
    /** Mute or unmute a spectator for the user only. */
    data class ToggleHearSpectator(val userId: String) : MatchMonoIntent
    data object OpenSettings : MatchMonoIntent
    data object ToggleEffects : MatchMonoIntent
    data object ToggleMusic : MatchMonoIntent
    data object OpenReport : MatchMonoIntent
    data class SelectReportPerson(val userId: String) : MatchMonoIntent
    data object SubmitReport : MatchMonoIntent
    data class Vote(val agree: Boolean) : MatchMonoIntent
    data object CloseSheet : MatchMonoIntent
    /** "End game (vote)" in the settings: asks first. */
    data object ProposeEnd : MatchMonoIntent
    /** "END THE GAME?" answered: start the vote, or not. */
    data object ConfirmEnd : MatchMonoIntent
    data object CancelEnd : MatchMonoIntent
    data class VoteEnd(val agree: Boolean) : MatchMonoIntent
    data object ExitClicked : MatchMonoIntent
    /** "Leave the match?" answered: leave, or stay. */
    data object ConfirmExit : MatchMonoIntent
    data object CancelExit : MatchMonoIntent
    /** The exit dialog's pick of who takes the seat (a CPU first, then each spectator). */
    data class PickSeatTaker(val index: Int) : MatchMonoIntent
    data class Leave(val giveToUserId: String?) : MatchMonoIntent
    data object StillHere : MatchMonoIntent
    /** An answer in a seat dialog. */
    data class Seat(val action: SeatHandoverAction) : MatchMonoIntent
    /** Long press on a player's seat (a bot's: the leader may offer it to a spectator). */
    data class PlayerLongPressed(val seat: Int) : MatchMonoIntent
    /** The screen went to the background: tell the server to play for the user. */
    data class ScreenVisible(val visible: Boolean) : MatchMonoIntent
}

sealed interface MatchMonoEffect : UiEffect {
    data object LeaveMatch : MatchMonoEffect
    /** Cards to fly across the table (plays, draws, penalties). */
    data class Fly(val flights: List<MonoFlight>) : MatchMonoEffect
    /** Turning the mic on: ask the phone for the mic (answers at once when already allowed). */
    data object RequestMicPermission : MatchMonoEffect
}
