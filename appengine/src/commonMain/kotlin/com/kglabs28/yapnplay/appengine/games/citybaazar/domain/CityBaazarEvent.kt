package com.kglabs28.yapnplay.appengine.games.citybaazar.domain

import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.MoneyReason

/** What happened since the last snapshot, in order (the table animates these). */
sealed interface CityBaazarEvent {
    data class Rolled(val seat: Int, val die1: Int, val die2: Int) : CityBaazarEvent
    /** [steps] negative going back; [jump] straight to jail. */
    data class Moved(val seat: Int, val from: Int, val to: Int, val steps: Int, val jump: Boolean) : CityBaazarEvent
    /** [from] / [to] null = the bank. */
    data class Money(val from: Int?, val to: Int?, val amount: Int, val reason: MoneyReason, val tile: Int?) : CityBaazarEvent
    data class Card(val seat: Int, val cardId: String) : CityBaazarEvent
    data class Bought(val seat: Int, val tile: Int) : CityBaazarEvent
    data class Built(val seat: Int, val tile: Int, val level: Int) : CityBaazarEvent
    data class Sold(val seat: Int, val tile: Int) : CityBaazarEvent
    data class Jailed(val seat: Int) : CityBaazarEvent
    data class Freed(val seat: Int) : CityBaazarEvent
    data class HalfRent(val seat: Int) : CityBaazarEvent
    data class AuctionStarted(val seller: Int, val tile: Int, val opening: Int) : CityBaazarEvent
    data class BidPlaced(val seat: Int, val tile: Int, val amount: Int) : CityBaazarEvent
    /** [winner] null: nobody bid, the bank bought it. */
    data class AuctionEnded(val seller: Int, val tile: Int, val winner: Int?, val amount: Int) : CityBaazarEvent
    data class Bankrupt(val seat: Int) : CityBaazarEvent
    data class Skipped(val seat: Int) : CityBaazarEvent
    data class TurnPassed(val seat: Int) : CityBaazarEvent
}
