package com.kglabs28.yapnplay.appengine.games.citybaazar.utils

/** What a Luck or Shagun Chest card does. */
sealed interface CardEffect {
    /** Gain (positive) or pay the bank (negative). */
    data class Money(val amount: Int) : CardEffect
    /** Move forward to [tile] (START money if passing it). */
    data class AdvanceTo(val tile: Int) : CardEffect
    data object GoToJail : CardEffect
    /** Keep it: leaves jail free once. */
    data object JailPass : CardEffect
    data class MoveBack(val steps: Int) : CardEffect
    data class CollectFromEach(val amount: Int) : CardEffect
    data class PayEach(val amount: Int) : CardEffect
    data class Repairs(val perHouse: Int, val perHotel: Int) : CardEffect
}
