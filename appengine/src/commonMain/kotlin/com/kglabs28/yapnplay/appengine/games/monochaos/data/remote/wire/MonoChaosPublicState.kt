package com.kglabs28.yapnplay.appengine.games.monochaos.data.remote.wire

import com.kglabs28.yapnplay.appengine.games.mono.data.remote.wire.MonoContinueVoteWire

import kotlinx.serialization.Serializable

/**
 * A MONO: Complete Chaos card on the wire: a [kind] name plus the fields
 * that kind needs. Prism cards carry no colour.
 */
@Serializable
data class MonoChaosCardWire(
    val kind: String,
    val color: String? = null,
    val number: Int? = null,
)

/**
 * `MatchSnapshot.publicState` for MONO: Complete Chaos. [hands] holds only
 * the viewer's own cards; every seat's size comes from [cardCounts].
 * [monoCallWindowExpiry] is epoch millis; 0 when shut. [eliminatedSeats]
 * holds seats knocked out at the 25-card threshold.
 */
@Serializable
data class MonoChaosPublicState(
    /** AWAITING_PLAY, AWAITING_COLOR_CHOICE, AWAITING_STACK_CHOICE, AWAITING_SWAP_CHOICE, AWAITING_ROULETTE_COLOR, AWAITING_ROULETTE_DRAW, AWAITING_DRAW_UNTIL_PLAYABLE or FINISHED. */
    val phase: String,
    val topCard: MonoChaosCardWire? = null,
    val topCardColor: String? = null,
    /** seat → that seat's cards. Only the viewer's own seat is ever sent. */
    val hands: Map<Int, List<MonoChaosCardWire>> = emptyMap(),
    /** seat → how many cards they hold. Sent for every seat. */
    val cardCounts: Map<Int, Int> = emptyMap(),
    val drawPileCount: Int = 0,
    val discardPileCount: Int = 0,
    /** Penalty waiting to be stacked onto or accepted; 0 when none. */
    val activeStackPenalty: Int = 0,
    val monoCallWindowExpiry: Long = 0,
    /** The seat whose MONO! window is open. */
    val monoWindowSeat: Int? = null,
    /** The seat holding the MONO! shield. */
    val monoCalledSeat: Int? = null,
    /** The value of the card that set the stack; a stacker must match it. */
    val stackTopValue: Int = 0,
    /** Direction of play; true means counter-clockwise. */
    val reversed: Boolean = false,
    /** After a draw, the only card the seat may play. */
    val drawnCardIndex: Int? = null,
    /** The seat that played a 7 and must pick a swap partner. */
    val pendingSwapSeat: Int? = null,
    val eliminatedSeats: List<Int> = emptyList(),
    val finishedSeats: List<Int> = emptyList(),
    /** Seats from first to last once the match is over. */
    val ranking: List<Int> = emptyList(),
    /** Holding this many cards knocks a seat out (a room / admin setting). */
    val knockoutThreshold: Int = 25,
    /** The seat that just went down to one card; CAUGHT is open on them until the next seat's turn ends. */
    val catchSeat: Int? = null,
    /** Seats that already paid a false catch in this window (one penalty per player). */
    val catchClosedFor: List<Int> = emptyList(),
    /** Two or more have finished: the humans still seated vote to keep playing or end; null when none is open. */
    val continueVote: MonoContinueVoteWire? = null,
    val caughtPenaltyCards: Int = 2,
    val falseCatchPenaltyCards: Int = 0,
)