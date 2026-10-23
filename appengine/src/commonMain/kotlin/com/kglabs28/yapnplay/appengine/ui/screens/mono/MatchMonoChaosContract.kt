package com.kglabs28.yapnplay.appengine.ui.screens.mono

import com.kglabs28.yapnplay.appengine.ui.components.table.MonoSwapTargetUi
import com.kglabs28.yapnplay.appengine.ui.components.moderation.TableSheetUi
import com.kglabs28.yapnplay.appengine.features.seathandover.presentation.LeaveDialogUi
import com.kglabs28.yapnplay.appengine.core.mvi.UiEffect
import com.kglabs28.yapnplay.appengine.core.mvi.UiIntent
import com.kglabs28.yapnplay.appengine.core.mvi.UiState
import com.kglabs28.yapnplay.appengine.games.monochaos.domain.MonoChaosCard
import com.kglabs28.yapnplay.appengine.games.monochaos.domain.MonoChaosMatch
import com.kglabs28.yapnplay.appengine.games.monochaos.identity.MonoChaosConstants
import com.kglabs28.yapnplay.appengine.ui.components.leaderboard.LeaderboardUi
import com.kglabs28.yapnplay.appengine.ui.components.table.MonoFlight
import com.kglabs28.yapnplay.appengine.ui.components.table.TableAvatarUi

/**
 * MONO: Complete Chaos table. The same table as Standard MONO — the two
 * games share the felt, the cards and the player pods — with stacking
 * and the 25-card knockout added. [activeStackPenalty] is the penalty on
 * the table the next player may stack onto or take.
 */
data class MatchMonoChaosState(
    val loaded: Boolean = false,
    val error: String? = null,
    val match: MonoChaosMatch? = null,
    val myTurn: Boolean = false,
    val selectedCardIndex: Int? = null,
    /**
     * Hand indexes the table will accept right now (or stack onto the
     * penalty on it). Everything else is drawn pushed back.
     */
    val playableIndexes: Set<Int> = emptySet(),
    /**
     * Hand indexes that may be stacked onto the penalty on the table. A
     * stacker needs a draw card worth at least as much as the last one
     * played.
     */
    val stackableIndexes: Set<Int> = emptySet(),
    /** True while the table waits for the user to stack or take the penalty. */
    val canStack: Boolean = false,
    /** True while the table waits for the user to pick the next colour. */
    val awaitingColorChoice: Boolean = false,
    /** True while the Rule of 7 waits for the user to pick a partner. */
    val awaitingSwapChoice: Boolean = false,
    /** True while the Roulette victim is naming the colour to inflict. */
    val awaitingRouletteChoice: Boolean = false,
    val activeStackPenalty: Int = 0,
    /** The user may call MONO now (their turn on two cards, or just down to one without a call). */
    val canCallMono: Boolean = false,
    /** Someone just went down to one card and the user may press CAUGHT on them. */
    val canCatch: Boolean = false,
    /** The user drew a playable card and may decline to play it. */
    val canPass: Boolean = false,
    /** "1st", "2nd"… as seats finish; seat → label. */
    val places: Map<Int, String> = emptyMap(),
    /** Seats knocked out at the 25-card threshold. */
    val eliminated: Set<Int> = emptySet(),
    /** 1 → 0 across the current turn, for the active card's border. */
    val timerFraction: Float = 1f,
    /** The turn countdown, "0:15". */
    val turnClock: String = "",
    /** The colour in force, which lights the table's ring. */
    val topColour: MonoChaosConstants.Color? = null,
    /** True when the turn runs counter-clockwise. */
    val reversed: Boolean = false,
    val title: String = "",
    val elapsed: String = "",
    val spectatorCount: Int = 0,
    val audience: List<TableAvatarUi> = emptyList(),
    /** Set once someone has emptied their hand or been left standing. */
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
    /** The keep-playing vote, once two or more have finished. */
    val vote: MonoVoteUi? = null,
    /** Settings (sound, music, report), a report to vote on, or its outcome; null when none is open. */
    val sheet: TableSheetUi? = null,
    /** Exit was tapped: "Leave the match?" waits for an answer (and who takes the seat). */
    val leaveDialog: LeaveDialogUi? = null,
    /** The final board, once the match is over (after the winner's banner has shown). */
    val leaderboard: LeaderboardUi? = null,
    /** "🏆 Riya finishes 1st!" for a few seconds after someone empties their hand. */
    val finishBanner: String? = null,
) : UiState {
    val hand get() = match?.me?.hand.orEmpty()
    /** The hand sorted by colour, power cards last (indexes into [hand]). */
    val handOrder: List<Int>
        get() = MonoUiMapper.handOrder(hand, { it.playColor()?.ordinal }, { it !is MonoChaosCard.NumberCard }, { (it as? MonoChaosCard.NumberCard)?.number ?: 0 })
    /** Everyone else, clockwise from the user's seat (the order turns go round the table). */
    /** Whom the Rule of 7 can trade hands with. */
    val swapTargets get() = opponents.filter { it.seat !in match?.eliminatedSeats.orEmpty() }.map { MonoSwapTargetUi(it.seat, it.name, it.avatarId.orEmpty(), it.cardCount) }

    /** Everyone else still in the game, clockwise from the user (a player who left takes no seat). */
    val opponents get() = MonoUiMapper.clockwise(match?.players.orEmpty().filter { !it.isMe && !it.removed }, match?.me?.seat) { it.seat }
}

sealed interface MatchMonoChaosIntent : UiIntent {
    data class Load(val matchId: String) : MatchMonoChaosIntent
    /** Taps a card. A legal card plays at once; an illegal one does nothing. */
    data class TapCard(val index: Int) : MatchMonoChaosIntent
    /** Draws until a card is playable (or the knockout threshold is hit). */
    data object DrawUntilPlayable : MatchMonoChaosIntent
    data class ChooseColor(val color: MonoChaosConstants.Color) : MatchMonoChaosIntent
    /** Prism Color Roulette: the user names the colour to inflict. */
    data class ChooseRouletteColor(val color: MonoChaosConstants.Color) : MatchMonoChaosIntent
    data object CallMono : MatchMonoChaosIntent
    /** Call out the seat that just went down to one card without calling MONO. */
    data object Caught : MatchMonoChaosIntent
    /** The keep-playing vote: true keeps the match going. */
    data class VoteContinue(val keepPlaying: Boolean) : MatchMonoChaosIntent
    /** Plays [index] as a stack onto the penalty already on the table. */
    data class StackDraw(val index: Int) : MatchMonoChaosIntent
    data object AcceptStack : MatchMonoChaosIntent
    /** End the turn after drawing a playable card and declining to play it. */
    data object Pass : MatchMonoChaosIntent
    /** The Rule of 7: the seat whose hand to trade with. */
    data class ChooseSwapTarget(val seat: Int) : MatchMonoChaosIntent
    data class SetAutoplay(val enabled: Boolean) : MatchMonoChaosIntent
    data class SetMic(val on: Boolean) : MatchMonoChaosIntent
    /** The user's speaker on or off (for them only). */
    data object ToggleSpeaker : MatchMonoChaosIntent
    /** Mute or unmute a player (by seat) for the user only. */
    data class ToggleHear(val seat: Int) : MatchMonoChaosIntent
    /** Mute or unmute a spectator for the user only. */
    data class ToggleHearSpectator(val userId: String) : MatchMonoChaosIntent
    data object OpenSettings : MatchMonoChaosIntent
    data object ToggleEffects : MatchMonoChaosIntent
    data object ToggleMusic : MatchMonoChaosIntent
    data object OpenReport : MatchMonoChaosIntent
    data class SelectReportPerson(val userId: String) : MatchMonoChaosIntent
    data object SubmitReport : MatchMonoChaosIntent
    data class Vote(val agree: Boolean) : MatchMonoChaosIntent
    data object CloseSheet : MatchMonoChaosIntent
    data object ExitClicked : MatchMonoChaosIntent
    /** "Leave the match?" answered: leave, or stay. */
    data object ConfirmExit : MatchMonoChaosIntent
    data object CancelExit : MatchMonoChaosIntent
    /** The exit dialog's pick of who takes the seat (a CPU first, then each spectator). */
    data class PickSeatTaker(val index: Int) : MatchMonoChaosIntent
    data class Leave(val giveToUserId: String?) : MatchMonoChaosIntent
    data object StillHere : MatchMonoChaosIntent
    /** The screen went to the background: tell the server to play for the user. */
    data class ScreenVisible(val visible: Boolean) : MatchMonoChaosIntent
}

sealed interface MatchMonoChaosEffect : UiEffect {
    data object LeaveMatch : MatchMonoChaosEffect
    /** Cards to fly across the table (plays, draws, penalties). */
    data class Fly(val flights: List<MonoFlight>) : MatchMonoChaosEffect
}
