package com.kglabs28.yapnplay.appengine.ui.screens.mono

import com.kglabs28.yapnplay.appengine.core.mvi.UiEffect
import com.kglabs28.yapnplay.appengine.core.mvi.UiIntent
import com.kglabs28.yapnplay.appengine.core.mvi.UiState
import com.kglabs28.yapnplay.appengine.games.mono.domain.MonoCard
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
    /** True while the Rule of 7 waits for the user to pick a partner. */
    val awaitingSwapChoice: Boolean = false,
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
    /** 1 → 0 across the current turn, for the active card's border. */
    val timerFraction: Float = 1f,
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
    val autoplay: Boolean = false,
    /** User ids talking right now (the blue-pink glow behind their seat). */
    val talking: Set<String> = emptySet(),
    /** The keep-playing vote, once two or more have finished. */
    val vote: MonoVoteUi? = null,
    /** The final board, once the match is over (after the winner's banner has shown). */
    val leaderboard: LeaderboardUi? = null,
    /** "🏆 Riya finishes 1st!" for a few seconds after someone empties their hand. */
    val finishBanner: String? = null,
) : UiState {
    val hand get() = match?.me?.hand.orEmpty()
    /** The hand sorted by colour, power cards last (indexes into [hand]). */
    val handOrder: List<Int>
        get() = MonoUiMapper.handOrder(hand, { it.playColor()?.ordinal }, { it.isPower() }, { (it as? MonoCard.NumberCard)?.number ?: 0 })
    /** Everyone else, clockwise from the user's seat (the order turns go round the table). */
    val opponents get() = MonoUiMapper.clockwise(match?.players.orEmpty().filter { !it.isMe }, match?.me?.seat) { it.seat }
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
    /** The Rule of 7: the seat whose hand to trade with. */
    data class ChooseSwapTarget(val seat: Int) : MatchMonoIntent
    data class SetAutoplay(val enabled: Boolean) : MatchMonoIntent
    data class SetMic(val on: Boolean) : MatchMonoIntent
    data object OpenSettings : MatchMonoIntent
    data object ExitClicked : MatchMonoIntent
    data class Leave(val giveToUserId: String?) : MatchMonoIntent
    data object StillHere : MatchMonoIntent
    /** The screen went to the background: tell the server to play for the user. */
    data class ScreenVisible(val visible: Boolean) : MatchMonoIntent
}

sealed interface MatchMonoEffect : UiEffect {
    data object OpenSettingsDialog : MatchMonoEffect
    data object LeaveMatch : MatchMonoEffect
    /** Cards to fly across the table (plays, draws, penalties). */
    data class Fly(val flights: List<MonoFlight>) : MatchMonoEffect
}
