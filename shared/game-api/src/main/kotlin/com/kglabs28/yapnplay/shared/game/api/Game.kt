package com.kglabs28.yapnplay.shared.game.api

/**
 * The game engine contract. One implementation per game module.
 *
 * Engines are PURE: no clock, no network, no hidden randomness.
 * Time and randomness are injected so tests are deterministic.
 */
interface Game {
    /** The game identifier (e.g., "ludo"). */
    val id: GameId
    /** Timing values this game declares. */
    val timing: TimingConfig
    /** Minimum players needed to start. */
    val minPlayers: Int
    /** Maximum players (the rest spectate). */
    val maxPlayers: Int

    /** The default rules for this game. */
    fun defaultRules(): GameRules

    /** Start a new match with [seatCount] players. */
    fun start(rules: GameRules, seatCount: Int, random: GameRandom): GameState

    /**
     * Apply a player action. Returns the new state plus events,
     * or a typed rejection if the action is not legal.
     */
    fun apply(
        state: GameState,
        seat: SeatNumber,
        action: Action,
        random: GameRandom,
    ): ApplyResult

    /** Whose turn it is. */
    fun currentActor(state: GameState): SeatNumber

    /** Automatically play the best move for [seat] when the timer expires. */
    fun autoplay(state: GameState, seat: SeatNumber): ApplyResult

    /** Remove a seat from the match (away player). */
    fun removeSeat(state: GameState, seat: SeatNumber): GameState

    /** What a specific viewer sees. */
    fun viewFor(state: GameState, viewer: Viewer): GameView

    /** Whether the match has ended. */
    fun isFinished(state: GameState): Boolean

    /** Final ranking once the match is finished. */
    fun outcome(state: GameState): MatchOutcome
}

/**
 * Result of applying an action or autoplay.
 */
sealed interface ApplyResult {
    data class Success(val state: GameState, val events: List<GameEvent>) : ApplyResult
    data class Rejected(val rejection: Rejection) : ApplyResult
}
