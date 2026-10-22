package com.kglabs28.yapnplay.appengine.games.mono.data.remote.wire

import kotlinx.serialization.Serializable

/**
 * A MONO card as it travels on the wire: a [kind] name plus the fields
 * that kind needs. Prism cards carry no colour.
 */
@Serializable
data class MonoCardWire(
    val kind: String,
    val color: String? = null,
    val number: Int? = null,
)

/**
 * `MatchSnapshot.publicState` for Standard MONO. [hands] holds only the
 * viewer's own cards; every seat's size comes from [cardCounts].
 * [monoCallWindowExpiry] is epoch millis; 0 when the window is shut.
 */
@Serializable
data class MonoPublicState(
    /** AWAITING_PLAY, AWAITING_COLOR_CHOICE, AWAITING_STACK_CHOICE, AWAITING_SWAP_CHOICE or FINISHED. */
    val phase: String,
    val topCard: MonoCardWire? = null,
    val topCardColor: String? = null,
    /** seat → that seat's cards. Only the viewer's own seat is ever sent. */
    val hands: Map<Int, List<MonoCardWire>> = emptyMap(),
    /** seat → how many cards they hold. Sent for every seat. */
    val cardCounts: Map<Int, Int> = emptyMap(),
    val drawPileCount: Int = 0,
    val discardPileCount: Int = 0,
    /** Penalty waiting to be stacked onto or accepted; 0 when none. */
    val activeStackPenalty: Int = 0,
    /** The value of the card that set the stack; a stacker must match it. */
    val stackTopValue: Int = 0,
    val monoCallWindowExpiry: Long = 0,
    /** The seat whose MONO! window is open. */
    val monoWindowSeat: Int? = null,
    /** The seat holding the MONO! shield. */
    val monoCalledSeat: Int? = null,
    /** Direction of play; true means counter-clockwise. */
    val reversed: Boolean = false,
    /** After a draw, the only card the seat may play. */
    val drawnCardIndex: Int? = null,
    /** Epoch millis when a playable drawn card stops being playable and the turn passes; 0 when none. */
    val drawnCardExpiry: Long = 0,
    /** The seat that played a 7 and must pick a swap partner. */
    val pendingSwapSeat: Int? = null,
    val eliminatedSeats: List<Int> = emptyList(),
    val finishedSeats: List<Int> = emptyList(),
    /** Seats from first to last once the match is over. */
    val ranking: List<Int> = emptyList(),
    /** The seat that just went down to one card; CAUGHT is open on them until the next seat's turn ends. */
    val catchSeat: Int? = null,
    /** Seats that already paid a false catch in this window (one penalty per player). */
    val catchClosedFor: List<Int> = emptyList(),
    /** Two or more have finished: the humans still seated vote to keep playing or end; null when none is open. */
    val continueVote: MonoContinueVoteWire? = null,
    /** What a genuine catch costs the caught seat. */
    val caughtPenaltyCards: Int = 2,
    /** What a false catch costs the seat that pressed CAUGHT. */
    val falseCatchPenaltyCards: Int = 0,
    /** House rule: MONO must be called on the player's own turn before the second-last card goes down. */
    val callBeforePlay: Boolean = false,
    /** House rule: a power card (anything but a number) can never be the last card played. */
    val noPowerFinish: Boolean = false,
)

/** A keep-playing vote: [voters] decide by [endsAt] (epoch ms); play waits meanwhile. */
@Serializable
data class MonoContinueVoteWire(
    val endsAt: Long,
    val voters: List<Int> = emptyList(),
    val yes: List<Int> = emptyList(),
    val no: List<Int> = emptyList(),
)
