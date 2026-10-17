package com.kglabs28.yapnplay.appengine.games.citybaazar.engine

import com.kglabs28.yapnplay.appengine.games.base.identity.GameId
import com.kglabs28.yapnplay.appengine.games.base.identity.SeatNumber
import com.kglabs28.yapnplay.appengine.games.base.match.GameState
import com.kglabs28.yapnplay.appengine.games.base.match.TurnState
import com.kglabs28.yapnplay.appengine.games.base.rules.GameRules

/**
 * A City Baazar match. [seats] are the players still in. [owners] tile →
 * seat; [levels] tile → 0 land, 1–3 houses, 4 hotel. [jailed] seats in
 * jail; [jailTriesLeft] rolls left this turn for the current seat;
 * [halfRent] seats collecting half rent until their next turn.
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
    val jailed: Set<Int> = emptySet(),
    val jailTriesLeft: Int = 0,
    val halfRent: Set<Int> = emptySet(),
    val skipNext: Set<Int> = emptySet(),
    val pendingTile: Int? = null,
    val debt: CityBaazarDebt? = null,
    val auction: CityBaazarAuction? = null,
    val bankruptOrder: List<Int> = emptyList(),
    val round: Int = 1,
    val turnCounter: Long = 0,
) : GameState {
    override val rules: GameRules get() = cbRules
    override val turn: TurnState get() = CityBaazarTurnState(SeatNumber(currentSeat), phase.name)

    val active: List<Int> get() = seats.map { it.value }

    fun cashOf(seat: Int): Int = cash[seat] ?: 0

    fun inJail(seat: Int): Boolean = seat in jailed

    fun level(tile: Int): Int = levels[tile] ?: 0
}
