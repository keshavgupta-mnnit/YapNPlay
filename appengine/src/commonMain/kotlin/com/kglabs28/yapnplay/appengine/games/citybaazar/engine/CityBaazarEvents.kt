package com.kglabs28.yapnplay.appengine.games.citybaazar.engine

import com.kglabs28.yapnplay.appengine.games.base.play.GameEvent
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.MoneyReason

data class DiceRolled(val seat: Int, val die1: Int, val die2: Int) : GameEvent
/** [steps] negative when going back; [jump] for jail (no walking). */
data class TokenMoved(val seat: Int, val from: Int, val to: Int, val steps: Int, val jump: Boolean) : GameEvent
/** [from]/[to] null = the bank. */
data class MoneyMoved(val from: Int?, val to: Int?, val amount: Int, val reason: MoneyReason, val tile: Int?) : GameEvent
data class TileBought(val seat: Int, val tile: Int, val price: Int) : GameEvent
data class BuildingBuilt(val seat: Int, val tile: Int, val level: Int, val cost: Int) : GameEvent
data class BuildingSold(val seat: Int, val tile: Int, val level: Int, val amount: Int) : GameEvent
data class TileSold(val seat: Int, val tile: Int, val amount: Int) : GameEvent
data class CardDrawn(val seat: Int, val cardId: String) : GameEvent
data class SentToJail(val seat: Int) : GameEvent
data class LeftJail(val seat: Int, val how: String) : GameEvent
data class RestTaken(val seat: Int) : GameEvent
data class TurnSkipped(val seat: Int) : GameEvent
data class DebtStarted(val seat: Int, val amount: Int) : GameEvent
data class WentBankrupt(val seat: Int) : GameEvent
data class TurnPassed(val seat: Int) : GameEvent
data class MatchEnded(val ranking: List<Int>) : GameEvent
