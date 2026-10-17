package com.kglabs28.yapnplay.appengine.games.citybaazar.engine

import com.kglabs28.yapnplay.appengine.games.base.identity.GameId
import com.kglabs28.yapnplay.appengine.games.base.identity.SeatNumber
import com.kglabs28.yapnplay.appengine.games.base.match.GameState
import com.kglabs28.yapnplay.appengine.games.base.match.TurnState
import com.kglabs28.yapnplay.appengine.games.base.rules.GameRules

/**
 * A City Baazar match. [seats] are the players still in (bankrupt or
 * removed seats drop out). [owners] tile → seat; [levels] tile → 0 (land),
 * 1–3 houses, 4 hotel. [jailTries] seats in jail → failed tries so far.
 * [pendingTile] is the tile a BUY / BUILD decision is about; [pendingMove]
 * a move still owed after a debt (third failed jail roll).
 */
data class CityBaazarState(
    override val gameId: GameId,
    val cbRules: CityBaazarRules,
    override val seats: List<SeatNumber>,
    val seatCount: Int,
    val positions: Map<Int, Int>,
    val cash: Map<Int, Int>,
    val owners: Map<Int, Int> = emptyMap(),
    val levels: Map<Int, Int> = emptyMap(),
    val currentSeat: Int,
    val phase: CityBaazarPhase,
    val die1: Int = 0,
    val die2: Int = 0,
    val rollAgain: Boolean = false,
    val doublesInRow: Int = 0,
    val jailTries: Map<Int, Int> = emptyMap(),
    val jailPasses: Map<Int, Int> = emptyMap(),
    val skipNext: Set<Int> = emptySet(),
    val pendingTile: Int? = null,
    val pendingMove: Int? = null,
    val debt: CityBaazarDebt? = null,
    val bankruptOrder: List<Int> = emptyList(),
    val round: Int = 1,
    val turnCounter: Long = 0,
) : GameState {
    override val rules: GameRules get() = cbRules
    override val turn: TurnState get() = CityBaazarTurnState(SeatNumber(currentSeat), phase.name)

    val active: List<Int> get() = seats.map { it.value }

    fun cashOf(seat: Int): Int = cash[seat] ?: 0

    fun inJail(seat: Int): Boolean = seat in jailTries

    fun level(tile: Int): Int = levels[tile] ?: 0
}
