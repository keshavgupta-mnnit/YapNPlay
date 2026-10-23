package com.kglabs28.yapnplay.appengine.games.monochaos.domain

import com.kglabs28.yapnplay.appengine.games.mono.domain.MonoContinueVote
import com.kglabs28.yapnplay.appengine.games.monochaos.identity.MonoChaosConstants
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/**
 * A MONO: Complete Chaos match as this viewer sees it. [topCard] is the
 * discard pile's face card; [hand] is only ever filled in for the
 * viewer's own seat, while [cardCount] is sent for everyone.
 * [eliminatedSeats] are the seats knocked out at the 25-card threshold.
 */
@Serializable
data class MonoChaosMatch(
    val matchId: String,
    val title: String,
    val gameId: String,
    val players: List<MonoChaosPlayer>,
    val spectators: List<MonoChaosSpectator>,
    val spectatorCount: Int,
    val currentSeat: Int,
    val phase: String,
    val topCard: MonoChaosCard?,
    val topCardColor: MonoChaosConstants.Color?,
    val drawPileCount: Int = 0,
    val discardPileCount: Int = 0,
    /** Penalty waiting to be stacked onto or accepted; 0 when none. */
    val activeStackPenalty: Int = 0,
    /** When the MONO! callout window shuts; [Instant] of the epoch start. */
    val monoCallWindowExpiry: Instant = Instant.fromEpochMilliseconds(0),
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
    val turnEndsAt: Instant? = null,
    val startedAt: Instant = Instant.fromEpochMilliseconds(0),
    val finishedSeats: List<Int> = emptyList(),
    val ranking: List<MonoChaosRankedSeat> = emptyList(),
    val seatOffer: MonoChaosSeatOffer? = null,
    /** The seat CAUGHT is open on (just went down to one card); null when shut. */
    val catchSeat: Int? = null,
    /** Seats that already paid a false catch in this window. */
    val catchClosedFor: List<Int> = emptyList(),
    /** The keep-playing vote, open once two or more have finished. */
    val continueVote: MonoContinueVote? = null,
    val caughtPenaltyCards: Int = 2,
    val falseCatchPenaltyCards: Int = 0,
    val roomId: String? = null,
    val viewerUserId: String,
) {
    val me: MonoChaosPlayer? get() = players.firstOrNull { it.isMe }

    fun player(seat: Int): MonoChaosPlayer? = players.firstOrNull { it.seat == seat }

    fun handForSeat(seat: Int): List<MonoChaosCard> = player(seat)?.hand ?: emptyList()

    fun cardCountForSeat(seat: Int): Int = player(seat)?.cardCount ?: 0

    /** True when the MONO! window is open and the viewer can still call it. */
    /** My own MONO! window is open: it opens after my play, so it is usually no longer my turn. */
    fun monoCallOpen(now: Instant): Boolean = me?.let { monoCallWindowExpiry > now && monoWindowSeat == it.seat && it.hand.size == 1 } == true
}

@Serializable
data class MonoChaosPlayer(
    val seat: Int,
    val userId: String?,
    val name: String,
    val level: Int,
    val isMe: Boolean,
    val micOn: Boolean,
    val autoplay: Boolean,
    val removed: Boolean,
    val isCpu: Boolean,
    val avatarId: String?,
    /** Only ever filled in for the viewer's own seat. */
    val hand: List<MonoChaosCard>,
    val cardCount: Int,
    val presence: String,
)

@Serializable
data class MonoChaosSpectator(val userId: String, val name: String, val micOn: Boolean, val avatarId: String = "")

@Serializable
data class MonoChaosRankedSeat(
    val seat: Int,
    val status: String,
    val place: Int,
)

@Serializable
data class MonoChaosSeatOffer(
    val offeredBy: Int,
    val offeredTo: String?,
    val expiresAt: Instant?,
)