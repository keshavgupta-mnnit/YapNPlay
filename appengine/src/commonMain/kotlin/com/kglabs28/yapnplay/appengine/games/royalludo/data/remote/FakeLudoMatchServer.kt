package com.kglabs28.yapnplay.appengine.games.royalludo.data.remote

import com.kglabs28.yapnplay.appengine.core.common.AppClock
import com.kglabs28.yapnplay.appengine.core.common.DispatcherProvider
import com.kglabs28.yapnplay.appengine.core.common.FakeDataProvider
import com.kglabs28.yapnplay.appengine.games.royalludo.domain.LudoColour
import com.kglabs28.yapnplay.appengine.games.royalludo.domain.LudoCommand
import com.kglabs28.yapnplay.appengine.games.royalludo.domain.LudoEvent
import com.kglabs28.yapnplay.appengine.games.royalludo.domain.LudoMatch
import com.kglabs28.yapnplay.appengine.games.royalludo.domain.LudoPhase
import com.kglabs28.yapnplay.appengine.games.royalludo.domain.LudoPlayer
import com.kglabs28.yapnplay.appengine.games.royalludo.domain.LudoSpectator
import com.kglabs28.yapnplay.appengine.games.royalludo.domain.LudoUpdate
import com.kglabs28.yapnplay.appengine.games.base.identity.SeatNumber
import com.kglabs28.yapnplay.appengine.games.base.play.Action
import com.kglabs28.yapnplay.appengine.games.base.play.ApplyResult
import com.kglabs28.yapnplay.appengine.games.base.play.DefaultGameRandom
import com.kglabs28.yapnplay.appengine.games.base.play.GameEvent
import com.kglabs28.yapnplay.appengine.games.elements.dice.mechanics.RollDice
import com.kglabs28.yapnplay.appengine.games.elements.pieces.mechanics.MovePiece
import com.kglabs28.yapnplay.appengine.games.elements.pieces.mechanics.PieceId
import com.kglabs28.yapnplay.appengine.games.royalludo.engine.DiceRolled
import com.kglabs28.yapnplay.appengine.games.royalludo.engine.Phase
import com.kglabs28.yapnplay.appengine.games.royalludo.engine.PieceCaptured
import com.kglabs28.yapnplay.appengine.games.royalludo.engine.PieceMoved
import com.kglabs28.yapnplay.appengine.games.royalludo.engine.RoyalLudoEngine
import com.kglabs28.yapnplay.appengine.games.royalludo.engine.RoyalLudoRules
import com.kglabs28.yapnplay.appengine.games.royalludo.engine.RoyalLudoState
import com.kglabs28.yapnplay.appengine.games.royalludo.engine.TurnForfeited
import com.kglabs28.yapnplay.appengine.games.royalludo.engine.TurnPassed
import com.kglabs28.yapnplay.appengine.games.royalludo.engine.TurnReverted
import com.kglabs28.yapnplay.appengine.games.royalludo.utils.ROYAL_LUDO_PLAYERS
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.datetime.Instant
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

/**
 * In-memory stand-in for the server's match actor: runs the real
 * [RoyalLudoEngine] on the device, plays the other three seats with
 * human-like pauses, autoplays a seat whose timer runs out (or whose
 * owner turned autoplay on) and streams every change with the events
 * that caused it. Deleted with the fakes.
 */
class FakeLudoMatchServer(
    private val clock: AppClock,
    dispatchers: DispatcherProvider,
) {
    private val scope = CoroutineScope(SupervisorJob() + dispatchers.default)
    private val engine = RoyalLudoEngine()
    private val random = DefaultGameRandom()
    private val mutex = Mutex()
    private val fake = FakeDataProvider.LudoMatch

    private var matchId: String? = null
    private lateinit var state: RoyalLudoState
    private var players: List<LudoPlayer> = emptyList()
    private var deadline: Instant? = null
    private var startedAt: Instant = Instant.DISTANT_PAST
    private var pauseMs = 0L
    private var loop: Job? = null

    private val updates = MutableSharedFlow<LudoUpdate>(replay = 1, extraBufferCapacity = UPDATE_BUFFER)
    /** Bumped on every change, so the turn loop wakes up early. */
    private val version = MutableStateFlow(0L)

    fun match(id: String): Flow<LudoUpdate> = flow {
        delay(FakeDataProvider.NETWORK_DELAY_MS)
        mutex.withLock { if (matchId != id) startMatch(id) }
        emitAll(updates)
    }

    /** Rotates "who is talking" among seated players with their mic on. */
    fun speaking(): Flow<Set<String>> = flow {
        var turn = 0
        while (true) {
            val talkers = players.filter { !it.isMe && it.micOn && !it.removed }
            emit(talkers.getOrNull(turn % talkers.size.coerceAtLeast(1))?.let { setOf(it.userId) }.orEmpty())
            turn++
            delay(FakeDataProvider.Lobby.SPEAKER_ROTATION_MS)
        }
    }

    suspend fun handle(command: LudoCommand) = mutex.withLock {
        val me = players.firstOrNull { it.isMe } ?: return@withLock
        when (command) {
            LudoCommand.Roll ->
                if (state.currentSeat == me.seat && state.phase == Phase.AWAITING_ROLL) apply(me.seat, RollDice)
            is LudoCommand.Move ->
                if (state.currentSeat == me.seat && state.phase == Phase.AWAITING_MOVE) {
                    apply(me.seat, MovePiece(PieceId(command.piece.toString())))
                }
            is LudoCommand.SetAutoplay -> {
                players = players.map { if (it.isMe) it.copy(autoplay = command.enabled) else it }
                publish(emptyList())
            }
            is LudoCommand.SetMic -> {
                players = players.map { if (it.isMe) it.copy(micOn = command.on) else it }
                publish(emptyList())
            }
            LudoCommand.Leave -> {
                state = engine.removeSeat(state, SeatNumber(me.seat)) as RoyalLudoState
                players = players.map { if (it.isMe) it.copy(removed = true) else it }
                loop?.cancel()
                publish(emptyList())
            }
        }
    }

    private fun startMatch(id: String) {
        loop?.cancel()
        matchId = id
        startedAt = clock.now()
        state = engine.start(RoyalLudoRules.TRADITIONAL, ROYAL_LUDO_PLAYERS, random) as RoyalLudoState
        val meProfile = FakeDataProvider.Profile.me
        players = (1..ROYAL_LUDO_PLAYERS).map { seat ->
            val colour = LudoColour.entries[state.colourForSeat(seat)]
            if (seat == MY_SEAT) {
                LudoPlayer(seat, meProfile.userId, meProfile.displayName, meProfile.level, colour, isMe = true, micOn = false, autoplay = false, removed = false)
            } else {
                val o = fake.opponents[seat - 2]
                LudoPlayer(seat, o.userId, o.name, o.level, colour, isMe = false, micOn = o.micOn, autoplay = false, removed = false)
            }
        }
        pauseMs = 0
        publish(emptyList())
        loop = scope.launch { turnLoop() }
    }

    /**
     * Plays whoever is up: other seats after a short "thinking" pause, my
     * seat when autoplay is on, or when my timer runs out. Any change
     * (my roll, my move, a toggle) wakes it to look again.
     */
    private suspend fun turnLoop() {
        while (scope.isActive) {
            val (seat, waitMs, seen) = mutex.withLock {
                if (state.phase == Phase.FINISHED) return
                val player = players.first { it.seat == state.currentSeat }
                val auto = !player.isMe || player.autoplay
                val untilDeadline = deadline?.let { (it - clock.now()).inWholeMilliseconds } ?: 0L
                Triple(state.currentSeat, if (auto) pauseMs + fake.THINK_MS else untilDeadline, version.value)
            }
            val changed = withTimeoutOrNull(waitMs.coerceAtLeast(0).milliseconds) { version.first { it != seen } }
            if (changed != null) continue
            mutex.withLock {
                if (version.value == seen && state.currentSeat == seat && state.phase != Phase.FINISHED) {
                    applyResult(engine.autoplay(state, SeatNumber(seat), random))
                }
            }
        }
    }

    private fun apply(seat: Int, action: Action) =
        applyResult(engine.apply(state, SeatNumber(seat), action, random))

    private fun applyResult(result: ApplyResult) {
        if (result !is ApplyResult.Success) return
        state = result.state as RoyalLudoState
        publish(result.events)
    }

    /** Sends the new snapshot; the next turn waits for its animations. */
    private fun publish(engineEvents: List<GameEvent>) {
        val events = engineEvents.mapNotNull { it.toLudoEvent() }
        // A toggle (mic, autoplay) keeps the pause of the last real play.
        if (events.isNotEmpty()) pauseMs = events.sumOf { event ->
            when (event) {
                is LudoEvent.Rolled -> fake.AFTER_ROLL_MS
                is LudoEvent.Moved -> fake.PER_STEP_MS * stepsOf(event) + fake.AFTER_MOVE_MS
                is LudoEvent.Captured -> fake.AFTER_CAPTURE_MS
                is LudoEvent.TurnPassed -> 0L
                is LudoEvent.Reverted -> fake.AFTER_CAPTURE_MS
            }
        }
        if (events.isNotEmpty() || deadline == null) {
            deadline = clock.now() + pauseMs.milliseconds + engine.timing.turnSeconds.seconds
        }
        version.value++
        updates.tryEmit(LudoUpdate(snapshot(), events))
    }

    private fun stepsOf(event: LudoEvent.Moved) = if (event.from < 0) 1 else event.to - event.from

    private fun snapshot(): LudoMatch {
        val id = matchId.orEmpty()
        val room = FakeDataProvider.PlayRooms.myRooms.rooms.firstOrNull { id.contains(it.roomId) }
        val finished = state.phase == Phase.FINISHED
        return LudoMatch(
            matchId = id,
            title = room?.name ?: fake.QUICK_PLAY_TITLE,
            players = players,
            spectators = fake.spectators.map { LudoSpectator(it.userId, it.name, it.micOn) },
            spectatorCount = fake.spectators.size,
            pieces = state.pieces,
            currentSeat = state.currentSeat,
            phase = when (state.phase) {
                Phase.AWAITING_ROLL -> LudoPhase.ROLL
                Phase.AWAITING_MOVE -> LudoPhase.MOVE
                Phase.FINISHED -> LudoPhase.FINISHED
            },
            lastRoll = state.lastRoll,
            legalPieces = state.legalPieces,
            turnEndsAt = if (finished) null else deadline,
            startedAt = startedAt,
            finishedSeats = state.finishingOrder,
            ranking = if (finished) engine.outcome(state).ranking.sortedBy { it.place }.map { it.seat.value } else emptyList(),
        )
    }

    private fun GameEvent.toLudoEvent(): LudoEvent? = when (this) {
        is DiceRolled -> LudoEvent.Rolled(seat, value)
        is PieceMoved -> LudoEvent.Moved(seat, piece, fromProgress, toProgress)
        is PieceCaptured -> LudoEvent.Captured(seat, victim, victimPiece)
        is TurnPassed -> LudoEvent.TurnPassed(seat)
        is TurnForfeited -> LudoEvent.TurnPassed(seat)
        is TurnReverted -> LudoEvent.Reverted(seat, pieces)
        else -> null
    }

    private companion object {
        /** The fake always seats the user first. */
        const val MY_SEAT = 1
        const val UPDATE_BUFFER = 64
    }
}
