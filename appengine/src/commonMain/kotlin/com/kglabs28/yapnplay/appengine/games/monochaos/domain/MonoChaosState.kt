package com.kglabs28.yapnplay.appengine.games.monochaos.domain

import com.kglabs28.yapnplay.appengine.games.base.identity.GameId
import com.kglabs28.yapnplay.appengine.games.base.identity.SeatNumber
import com.kglabs28.yapnplay.appengine.games.base.match.GameState
import com.kglabs28.yapnplay.appengine.games.base.match.TurnState
import com.kglabs28.yapnplay.appengine.games.base.rules.GameRules
import com.kglabs28.yapnplay.appengine.games.monochaos.domain.rules.MonoChaosRules
import com.kglabs28.yapnplay.appengine.games.monochaos.identity.MonoChaosConstants
import kotlinx.serialization.Serializable

@Serializable
data class MonoChaosState(
    private val _gameId: GameId,
    private val _rules: MonoChaosRules,
    private val _seats: List<SeatNumber>,
    private val _turn: MonoChaosTurnState,
    val hands: Map<Int, List<MonoChaosCard>>,
    val drawPile: List<MonoChaosCard>,
    val discardPile: List<MonoChaosCard>,
    val currentSeat: Int,
    val phase: MonoChaosPhase,
    val topCardColor: MonoChaosConstants.Color?,
    val turnCounter: Long = 0,
    val monoCallWindowExpiry: Long = 0,
    val lastActionSeat: Int? = null,
    val lastActionCard: MonoChaosCard? = null,
    val activeStackPenalty: Int = 0,
    val eliminatedSeats: Set<Int> = emptySet(),
    /** Seats that left the table; kept so the final ranking can place them. */
    val removedSeats: Set<Int> = emptySet(),
) : GameState {
    override val gameId: GameId get() = _gameId
    override val rules: GameRules get() = _rules
    override val seats: List<SeatNumber> get() = _seats
    override val turn: TurnState get() = _turn
    val monoChaosRules: MonoChaosRules get() = _rules

    fun handForSeat(seat: Int): List<MonoChaosCard> = hands[seat] ?: emptyList()
    fun cardCountForSeat(seat: Int): Int = hands[seat]?.size ?: 0
    fun isEliminated(seat: Int): Boolean = eliminatedSeats.contains(seat)

    fun copy(
        rules: MonoChaosRules = this._rules,
        seats: List<SeatNumber> = this._seats,
        turn: MonoChaosTurnState = this._turn,
        hands: Map<Int, List<MonoChaosCard>> = this.hands,
        drawPile: List<MonoChaosCard> = this.drawPile,
        discardPile: List<MonoChaosCard> = this.discardPile,
        currentSeat: Int = this.currentSeat,
        phase: MonoChaosPhase = this.phase,
        topCardColor: MonoChaosConstants.Color? = this.topCardColor,
        turnCounter: Long = this.turnCounter,
        monoCallWindowExpiry: Long = this.monoCallWindowExpiry,
        lastActionSeat: Int? = this.lastActionSeat,
        lastActionCard: MonoChaosCard? = this.lastActionCard,
        activeStackPenalty: Int = this.activeStackPenalty,
        eliminatedSeats: Set<Int> = this.eliminatedSeats,
        removedSeats: Set<Int> = this.removedSeats,
    ): MonoChaosState = MonoChaosState(
        _gameId, _rules, _seats, _turn, hands, drawPile, discardPile,
        currentSeat, phase, topCardColor, turnCounter, monoCallWindowExpiry,
        lastActionSeat, lastActionCard, activeStackPenalty, eliminatedSeats, removedSeats,
    )
}
