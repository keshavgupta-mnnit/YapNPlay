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
    /** AWAITING_PLAY, AWAITING_COLOR_CHOICE, AWAITING_STACK_CHOICE or FINISHED. */
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
    val monoCallWindowExpiry: Long = 0,
    val eliminatedSeats: List<Int> = emptyList(),
    val finishedSeats: List<Int> = emptyList(),
    /** Seats from first to last once the match is over. */
    val ranking: List<Int> = emptyList(),
)