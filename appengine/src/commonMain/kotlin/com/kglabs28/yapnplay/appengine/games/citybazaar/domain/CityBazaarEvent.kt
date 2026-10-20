package com.kglabs28.yapnplay.appengine.games.citybazaar.domain

import com.kglabs28.yapnplay.appengine.games.citybazaar.utils.MoneyReason

/** What happened since the last snapshot, in order (the table animates these). */
sealed interface CityBazaarEvent {
    data class Rolled(val seat: Int, val die1: Int, val die2: Int) : CityBazaarEvent
    /** [steps] negative going back; [jump] straight to jail. */
    data class Moved(val seat: Int, val from: Int, val to: Int, val steps: Int, val jump: Boolean) : CityBazaarEvent
    /** [from] / [to] null = the bank. */
    data class Money(val from: Int?, val to: Int?, val amount: Int, val reason: MoneyReason, val tile: Int?) : CityBazaarEvent
    data class Card(val seat: Int, val cardId: String) : CityBazaarEvent
    data class Bought(val seat: Int, val tile: Int) : CityBazaarEvent
    /** Landed on an unowned tile without the money to buy it. */
    data class CantAfford(val seat: Int, val tile: Int) : CityBazaarEvent
    data class Built(val seat: Int, val tile: Int, val level: Int) : CityBazaarEvent
    data class Sold(val seat: Int, val tile: Int) : CityBazaarEvent
    data class Jailed(val seat: Int) : CityBazaarEvent
    data class Freed(val seat: Int) : CityBazaarEvent
    data class HalfRent(val seat: Int) : CityBazaarEvent
    data class AuctionStarted(val seller: Int, val tile: Int, val opening: Int) : CityBazaarEvent
    data class BidPlaced(val seat: Int, val tile: Int, val amount: Int) : CityBazaarEvent
    /** [winner] null: nobody bid, the bank bought it. */
    data class AuctionEnded(val seller: Int, val tile: Int, val winner: Int?, val amount: Int) : CityBazaarEvent
    data class Bankrupt(val seat: Int) : CityBazaarEvent
    data class Skipped(val seat: Int) : CityBazaarEvent
    data class TurnPassed(val seat: Int) : CityBazaarEvent
    /** Landed on Rest House: their next turn is skipped. */
    data class Rested(val seat: Int) : CityBazaarEvent
    /** The continue-or-end vote is decided. */
    data class ExtensionVoted(val continued: Boolean, val yes: Int, val voters: Int) : CityBazaarEvent
}
