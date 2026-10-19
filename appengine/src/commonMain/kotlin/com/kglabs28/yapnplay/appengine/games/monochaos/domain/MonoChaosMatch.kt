package com.kglabs28.yapnplay.appengine.games.monochaos.domain

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
    val eliminatedSeats: List<Int> = emptyList(),
    val turnEndsAt: Instant? = null,
    val startedAt: Instant = Instant.fromEpochMilliseconds(0),
    val finishedSeats: List<Int> = emptyList(),
    val ranking: List<MonoChaosRankedSeat> = emptyList(),
    val seatOffer: MonoChaosSeatOffer? = null,
    val roomId: String? = null,
    val viewerUserId: String,
) {
    val me: MonoChaosPlayer? get() = players.firstOrNull { it.isMe }

    fun player(seat: Int): MonoChaosPlayer? = players.firstOrNull { it.seat == seat }

    fun handForSeat(seat: Int): List<MonoChaosCard> = player(seat)?.hand ?: emptyList()

    fun cardCountForSeat(seat: Int): Int = player(seat)?.cardCount ?: 0

    /** True when the MONO! window is open and the viewer can still call it. */
    fun monoCallOpen(now: Instant): Boolean = monoCallWindowExpiry > now && (me?.hand?.size ?: 0) == 1
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
data class MonoChaosSpectator(val userId: String, val name: String, val micOn: Boolean)

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