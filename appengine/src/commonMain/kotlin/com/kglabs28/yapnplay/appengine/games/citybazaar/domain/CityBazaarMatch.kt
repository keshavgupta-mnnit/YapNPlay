package com.kglabs28.yapnplay.appengine.games.citybazaar.domain

import com.kglabs28.yapnplay.appengine.features.seathandover.domain.SeatOffer
import com.kglabs28.yapnplay.appengine.games.base.match.MatchEndVote
import kotlinx.datetime.Instant

/**
 * The live match, rebuilt from every server snapshot. [owners] tile → seat,
 * [levels] tile → 0 land, 1–3 houses, 4 hotel. Times are on this device's
 * clock; [timeEndsAt] is the match time limit (null: off). [roomId] is null
 * for random tables (no spectators).
 */
data class CityBazaarMatch(
    val matchId: String,
    val title: String,
    val players: List<CityBazaarPlayer>,
    val spectators: List<CityBazaarSpectator>,
    val spectatorCount: Int,
    val owners: Map<Int, Int>,
    val levels: Map<Int, Int>,
    val currentSeat: Int,
    val phase: CityBazaarTurnPhase,
    val die1: Int,
    val die2: Int,
    val pendingTile: Int?,
    val debt: CityBazaarDebtInfo?,
    /** Rolls for doubles left this turn (in jail). */
    val jailTriesLeft: Int,
    val auction: CityBazaarAuctionInfo?,
    val turnEndsAt: Instant?,
    val startedAt: Instant,
    val timeEndsAt: Instant?,
    val round: Int,
    /** Rounds before the match ends on net worth, or null (off). */
    val roundLimit: Int?,
    /** In debt, a bare city goes up for auction (house rule on) instead of straight to the bank. */
    val debtAuction: Boolean = true,
    /** The room's money-popup rule: the user's payments as a popup (off: a quick coin animation). */
    val moneyPopups: Boolean = true,
    val netWorth: Map<Int, Int>,
    /** Seats from first to last once the match is over. */
    val ranking: List<Int>,
    /** The coin pot of a paid league (0 for free games). */
    val pot: Int = 0,
    val roomId: String? = null,
    val viewerUserId: String = "",
    /** Tile → all the rent it has earned so far. */
    val rentCollected: Map<Int, Int> = emptyMap(),
    val extensionVote: CityBazaarExtensionVoteInfo? = null,
    /** The end-game vote while it runs. */
    val endVote: MatchEndVote? = null,
    val bank: CityBazaarBankInfo? = null,
    /** A seat changing hands (room matches only); null when none. */
    val seatOffer: SeatOffer? = null,
    /** The viewer leads the room (or is its admin): they may offer a bot's seat. */
    val canOfferSeats: Boolean = false,
) {
    val me: CityBazaarPlayer? get() = players.firstOrNull { it.isMe }
    val current: CityBazaarPlayer? get() = players.firstOrNull { it.seat == currentSeat }
    val isRoomMatch: Boolean get() = roomId != null
    fun player(seat: Int): CityBazaarPlayer? = players.firstOrNull { it.seat == seat }
}
