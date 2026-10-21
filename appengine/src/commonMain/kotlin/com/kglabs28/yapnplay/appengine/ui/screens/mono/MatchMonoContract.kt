package com.kglabs28.yapnplay.appengine.ui.screens.mono

import com.kglabs28.yapnplay.appengine.core.mvi.UiEffect
import com.kglabs28.yapnplay.appengine.core.mvi.UiIntent
import com.kglabs28.yapnplay.appengine.core.mvi.UiState
import com.kglabs28.yapnplay.appengine.games.mono.domain.MonoMatch
import com.kglabs28.yapnplay.appengine.games.mono.identity.MonoConstants
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
    /** The MONO! callout is open and the user is down to one card. */
    val canCallMono: Boolean = false,
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
) : UiState {
    val hand get() = match?.me?.hand.orEmpty()
    val opponents get() = match?.players.orEmpty().filter { !it.isMe }
}

sealed interface MatchMonoIntent : UiIntent {
    data class Load(val matchId: String) : MatchMonoIntent
    /** Taps a card. A legal card plays at once; an illegal one does nothing. */
    data class TapCard(val index: Int) : MatchMonoIntent
    data object DrawCard : MatchMonoIntent
    data class ChooseColor(val color: MonoConstants.Color) : MatchMonoIntent
    data object CallMono : MatchMonoIntent
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
}
