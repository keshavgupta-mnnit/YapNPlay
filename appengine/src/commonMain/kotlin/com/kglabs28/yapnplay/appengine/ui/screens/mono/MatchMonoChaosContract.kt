package com.kglabs28.yapnplay.appengine.ui.screens.mono

import com.kglabs28.yapnplay.appengine.core.mvi.UiEffect
import com.kglabs28.yapnplay.appengine.core.mvi.UiIntent
import com.kglabs28.yapnplay.appengine.core.mvi.UiState
import com.kglabs28.yapnplay.appengine.games.monochaos.domain.MonoChaosMatch
import com.kglabs28.yapnplay.appengine.games.monochaos.identity.MonoChaosConstants
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
    /** The MONO! callout is open and the user is down to one card. */
    val canCallMono: Boolean = false,
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
    val autoplay: Boolean = false,
) : UiState {
    val hand get() = match?.me?.hand.orEmpty()
    val opponents get() = match?.players.orEmpty().filter { !it.isMe }
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
    /** Plays [index] as a stack onto the penalty already on the table. */
    data class StackDraw(val index: Int) : MatchMonoChaosIntent
    data object AcceptStack : MatchMonoChaosIntent
    /** End the turn after drawing a playable card and declining to play it. */
    data object Pass : MatchMonoChaosIntent
    /** The Rule of 7: the seat whose hand to trade with. */
    data class ChooseSwapTarget(val seat: Int) : MatchMonoChaosIntent
    data class SetAutoplay(val enabled: Boolean) : MatchMonoChaosIntent
    data class SetMic(val on: Boolean) : MatchMonoChaosIntent
    data object OpenSettings : MatchMonoChaosIntent
    data object ExitClicked : MatchMonoChaosIntent
    data class Leave(val giveToUserId: String?) : MatchMonoChaosIntent
    data object StillHere : MatchMonoChaosIntent
    /** The screen went to the background: tell the server to play for the user. */
    data class ScreenVisible(val visible: Boolean) : MatchMonoChaosIntent
}

sealed interface MatchMonoChaosEffect : UiEffect {
    data object OpenSettingsDialog : MatchMonoChaosEffect
    data object LeaveMatch : MatchMonoChaosEffect
}
