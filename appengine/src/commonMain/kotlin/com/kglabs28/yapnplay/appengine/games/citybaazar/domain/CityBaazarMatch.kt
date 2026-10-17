package com.kglabs28.yapnplay.appengine.games.citybaazar.domain

import kotlinx.datetime.Instant

/**
 * The live match, rebuilt from every server snapshot. [owners] tile → seat,
 * [levels] tile → 0 land, 1–3 houses, 4 hotel. Times are on this device's
 * clock; [timeEndsAt] is the match time limit (null: off). [roomId] is null
 * for random tables (no spectators).
 */
data class CityBaazarMatch(
    val matchId: String,
    val title: String,
    val players: List<CityBaazarPlayer>,
    val spectators: List<CityBaazarSpectator>,
    val spectatorCount: Int,
    val owners: Map<Int, Int>,
    val levels: Map<Int, Int>,
    val currentSeat: Int,
    val phase: CityBaazarTurnPhase,
    val die1: Int,
    val die2: Int,
    val pendingTile: Int?,
    val debt: CityBaazarDebtInfo?,
    /** Rolls for doubles left this turn (in jail). */
    val jailTriesLeft: Int,
    val auction: CityBaazarAuctionInfo?,
    val turnEndsAt: Instant?,
    val startedAt: Instant,
    val timeEndsAt: Instant?,
    val round: Int,
    /** Rounds before the match ends on net worth, or null (off). */
    val roundLimit: Int?,
    val netWorth: Map<Int, Int>,
    /** Seats from first to last once the match is over. */
    val ranking: List<Int>,
    val roomId: String? = null,
    val viewerUserId: String = "",
) {
    val me: CityBaazarPlayer? get() = players.firstOrNull { it.isMe }
    val current: CityBaazarPlayer? get() = players.firstOrNull { it.seat == currentSeat }
    val isRoomMatch: Boolean get() = roomId != null
    fun player(seat: Int): CityBaazarPlayer? = players.firstOrNull { it.seat == seat }
}
