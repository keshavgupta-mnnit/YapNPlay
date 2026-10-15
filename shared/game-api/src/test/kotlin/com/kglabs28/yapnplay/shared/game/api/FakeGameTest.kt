package com.kglabs28.yapnplay.shared.game.api

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

/**
 * FakeGame proves the Game interface is usable end-to-end:
 * start → apply → autoplay → removeSeat → viewFor → outcome.
 */
class FakeGameTest {

    private val game = FakeGame()
    private val random = ScriptedRandom()

    @Test
    fun `start creates state with correct seats and timing`() {
        val state = game.start(game.defaultRules(), seatCount = 2, random)

        assertEquals(2, state.seats.size)
        assertEquals(SeatNumber(1), state.seats[0])
        assertEquals(SeatNumber(2), state.seats[1])
        assertEquals(30, game.timing.turnSeconds)
        assertEquals(2, game.minPlayers)
        assertEquals(4, game.maxPlayers)
    }

    @Test
    fun `apply returns new state and events on success`() {
        val state = game.start(game.defaultRules(), seatCount = 2, random)
        val result = game.apply(state, SeatNumber(1), Action.RollDice, random)

        assertIs<ApplyResult.Success>(result)
        assertEquals(1, result.events.size)
    }

    @Test
    fun `apply returns rejection when not your turn`() {
        val state = game.start(game.defaultRules(), seatCount = 2, random)
        val result = game.apply(state, SeatNumber(2), Action.RollDice, random)

        assertIs<ApplyResult.Rejected>(result)
        assertEquals("NOT_YOUR_TURN", result.rejection.code)
    }

    @Test
    fun `autoplay returns success with events`() {
        val state = game.start(game.defaultRules(), seatCount = 2, random)
        val result = game.autoplay(state, SeatNumber(1))

        assertIs<ApplyResult.Success>(result)
        assertTrue(result.events.isNotEmpty())
    }

    @Test
    fun `removeSeat removes the seat from active seats`() {
        val state = game.start(game.defaultRules(), seatCount = 3, random)
        val newState = game.removeSeat(state, SeatNumber(2))

        assertEquals(2, newState.seats.size)
        assertFalse(newState.seats.contains(SeatNumber(2)))
    }

    @Test
    fun `viewFor returns a view`() {
        val state = game.start(game.defaultRules(), seatCount = 2, random)
        val view = game.viewFor(state, Viewer.Player(SeatNumber(1)))

        assertIs<FakeGameView>(view)
    }

    @Test
    fun `isFinished returns false initially`() {
        val state = game.start(game.defaultRules(), seatCount = 2, random)
        assertFalse(game.isFinished(state))
    }

    @Test
    fun `outcome returns ranking for all seats`() {
        val state = game.start(game.defaultRules(), seatCount = 2, random)
        val outcome = game.outcome(state)

        assertEquals(2, outcome.ranking.size)
        assertEquals(SeatNumber(1), outcome.ranking[0].seat)
    }

    @Test
    fun `SeatNumber rejects zero`() {
        assertThrows<IllegalArgumentException> {
            SeatNumber(0)
        }
    }

    @Test
    fun `currentActor returns the acting seat`() {
        val state = game.start(game.defaultRules(), seatCount = 2, random)
        assertEquals(SeatNumber(1), game.currentActor(state))
    }
}

// ── Fake Game Implementation ──────────────────────────────────────────────

private class FakeGame : Game {
    override val id = GameId("fake")
    override val timing = TimingConfig()
    override val minPlayers = 2
    override val maxPlayers = 4

    override fun defaultRules(): GameRules = FakeRules

    override fun start(rules: GameRules, seatCount: Int, random: GameRandom): GameState {
        val seats = (1..seatCount).map { SeatNumber(it) }
        return FakeState(seats = seats, events = mutableListOf())
    }

    override fun apply(
        state: GameState,
        seat: SeatNumber,
        action: Action,
        random: GameRandom,
    ): ApplyResult {
        val fake = state as FakeState
        if (fake.turn.seat != seat) {
            return ApplyResult.Rejected(TypedRejection("NOT_YOUR_TURN", "Not your turn"))
        }
        return ApplyResult.Success(fake, listOf(FakeEvent("action")))
    }

    override fun currentActor(state: GameState): SeatNumber = (state as FakeState).turn.seat

    override fun autoplay(state: GameState, seat: SeatNumber): ApplyResult {
        val fake = state as FakeState
        return ApplyResult.Success(fake, listOf(FakeEvent("autoplay")))
    }

    override fun removeSeat(state: GameState, seat: SeatNumber): GameState {
        val fake = state as FakeState
        return fake.copy(seats = fake.seats.filter { it != seat })
    }

    override fun viewFor(state: GameState, viewer: Viewer): GameView = FakeGameView

    override fun isFinished(state: GameState): Boolean = (state as FakeState).seats.size <= 1

    override fun outcome(state: GameState): MatchOutcome {
        val fake = state as FakeState
        val ranking = fake.seats.mapIndexed { index, seat ->
            RankedSeat(seat = seat, status = SeatStatus.FINISHED, place = index + 1)
        }
        return MatchOutcome(ranking)
    }
}

private data class FakeState(
    override val gameId: GameId = GameId("fake"),
    override val rules: GameRules = FakeRules,
    override val seats: List<SeatNumber>,
    override val turn: TurnState = FakeTurnState(SeatNumber(1)),
) : GameState

private data class FakeTurnState(
    override val seat: SeatNumber,
    override val phase: String = "ROLL",
) : TurnState

private class FakeEvent(val type: String) : GameEvent

private data object FakeRules : GameRules

private data object FakeGameView : GameView

// ── Scripted Random ───────────────────────────────────────────────────────

/**
 * A scripted random that returns predetermined values.
 * Tests set up the sequence they need; no hidden randomness.
 */
private class ScriptedRandom : GameRandom {
    private val values = ArrayDeque(listOf(3, 1, 4, 1, 5))
    override fun nextInt(bound: Int): Int {
        if (values.isEmpty()) return 0
        return values.removeFirst() % bound
    }
}
