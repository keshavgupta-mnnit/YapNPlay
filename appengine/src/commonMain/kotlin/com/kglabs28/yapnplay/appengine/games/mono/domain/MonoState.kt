package com.kglabs28.yapnplay.appengine.games.mono.domain

import com.kglabs28.yapnplay.appengine.games.base.identity.GameId
import com.kglabs28.yapnplay.appengine.games.base.identity.SeatNumber
import com.kglabs28.yapnplay.appengine.games.base.match.GameState
import com.kglabs28.yapnplay.appengine.games.base.match.TurnState
import com.kglabs28.yapnplay.appengine.games.base.rules.GameRules
import com.kglabs28.yapnplay.appengine.games.mono.domain.rules.MonoRules
import com.kglabs28.yapnplay.appengine.games.mono.identity.MonoConstants
import kotlinx.serialization.Serializable

/**
 * A Standard MONO match. [hands] seat → that seat's cards, [drawPile] is
 * what is left to draw and [discardPile] the face-up pile whose last card
 * is the one to match. [activeStackPenalty] is a draw penalty waiting to
 * be stacked onto or taken.
 */
@Serializable
data class MonoState(
    private val _gameId: GameId,
    private val _rules: MonoRules,
    private val _seats: List<SeatNumber>,
    private val _turn: MonoTurnState,
    val hands: Map<Int, List<MonoCard>>,
    val drawPile: List<MonoCard>,
    val discardPile: List<MonoCard>,
    val currentSeat: Int,
    val phase: MonoPhase,
    val topCardColor: MonoConstants.Color?,
    val turnCounter: Long = 0,
    /** Deadline for the MONO! callout; 0 when the window is shut. */
    val monoCallWindowExpiry: Long = 0,
    val lastActionSeat: Int? = null,
    val lastActionCard: MonoCard? = null,
    /** Penalty waiting to be stacked onto or taken; 0 when none. */
    val activeStackPenalty: Int = 0,
    val eliminatedSeats: Set<Int> = emptySet(),
    /** Seats that left the table; kept so the final ranking can place them. */
    val removedSeats: Set<Int> = emptySet(),
) : GameState {
    override val gameId: GameId get() = _gameId
    override val rules: GameRules get() = _rules
    override val seats: List<SeatNumber> get() = _seats
    override val turn: TurnState get() = _turn
    val monoRules: MonoRules get() = _rules

    fun handForSeat(seat: Int): List<MonoCard> = hands[seat] ?: emptyList()
    fun cardCountForSeat(seat: Int): Int = hands[seat]?.size ?: 0
    fun isEliminated(seat: Int): Boolean = seat in eliminatedSeats

    fun copy(
        rules: MonoRules = this._rules,
        seats: List<SeatNumber> = this._seats,
        turn: MonoTurnState = this._turn,
        hands: Map<Int, List<MonoCard>> = this.hands,
        drawPile: List<MonoCard> = this.drawPile,
        discardPile: List<MonoCard> = this.discardPile,
        currentSeat: Int = this.currentSeat,
        phase: MonoPhase = this.phase,
        topCardColor: MonoConstants.Color? = this.topCardColor,
        turnCounter: Long = this.turnCounter,
        monoCallWindowExpiry: Long = this.monoCallWindowExpiry,
        lastActionSeat: Int? = this.lastActionSeat,
        lastActionCard: MonoCard? = this.lastActionCard,
        activeStackPenalty: Int = this.activeStackPenalty,
        eliminatedSeats: Set<Int> = this.eliminatedSeats,
        removedSeats: Set<Int> = this.removedSeats,
    ): MonoState = MonoState(
        _gameId, _rules, _seats, _turn, hands, drawPile, discardPile,
        currentSeat, phase, topCardColor, turnCounter, monoCallWindowExpiry,
        lastActionSeat, lastActionCard, activeStackPenalty, eliminatedSeats, removedSeats,
    )
}