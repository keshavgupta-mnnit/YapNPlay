package com.kglabs28.yapnplay.appengine.games.citybazaar.data.remote.wire

import kotlinx.serialization.Serializable

/**
 * `MatchSnapshot.publicState` for City Bazaar. [owners] tile → seat,
 * [levels] tile → 0 land, 1–3 houses, 4 hotel. Times are epoch millis.
 */
@Serializable
data class CityBazaarPublicState(
    val players: List<CityBazaarSeatState>,
    val owners: Map<Int, Int>,
    val levels: Map<Int, Int>,
    /** ROLL, JAIL_ROLL, JAIL_CHOICE, BUY, BUILD, DEBT, AUCTION or FINISHED. */
    val phase: String,
    val die1: Int,
    val die2: Int,
    val pendingTile: Int? = null,
    val debt: CityBazaarDebt? = null,
    val jailTriesLeft: Int = 0,
    val auction: CityBazaarAuction? = null,
    /** When the match time limit ends; null when off. */
    val timeEndsAt: Long? = null,
    val round: Int,
    /** House rule debt_auction: in debt, a bare city goes up for auction (false: straight to the bank). */
    val debtAuction: Boolean = true,
    val roundLimit: Int? = null,
    val netWorth: Map<Int, Int>,
    /** Seats from first to last once the match is over. */
    val ranking: List<Int> = emptyList(),
    /** Tile → all the rent it has earned its owners so far this match. */
    val rentCollected: Map<Int, Int> = emptyMap(),
    /** The bank's money (it pays and receives like a player). */
    val bank: CityBazaarBank? = null,
)

/** One seat's money and position. */
@Serializable
data class CityBazaarSeatState(
    val seat: Int,
    val cash: Int,
    val position: Int,
    val inJail: Boolean,
    val halfRent: Boolean,
    val bankrupt: Boolean,
    /** All the rent this player has collected so far this match. */
    val rentCollected: Int = 0,
    /** All the rent this player has paid so far (club / rest house payments not included). */
    val rentPaid: Int = 0,
)

/** What the current player owes but can't pay yet. [reason] is a MoneyReason name. */
@Serializable
data class CityBazaarDebt(val amount: Int, val reason: String, val tile: Int? = null)

/** A running auction: highest [bid] by [leaderSeat] (null: no bid yet, it opens at [bid]). */
@Serializable
data class CityBazaarAuction(
    val tile: Int,
    val sellerSeat: Int,
    val bid: Int,
    val leaderSeat: Int? = null,
    val nextBid: Int,
    val endsAt: Long,
)

/** The bank: what it holds now, and all it has received from and paid to players. */
@Serializable
data class CityBazaarBank(val cash: Long, val received: Long, val paid: Long)
