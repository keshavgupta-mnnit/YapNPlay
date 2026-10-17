package com.kglabs28.yapnplay.appengine.games.citybaazar.data.remote

import com.kglabs28.yapnplay.appengine.core.common.AppClock
import com.kglabs28.yapnplay.appengine.core.common.DispatcherProvider
import com.kglabs28.yapnplay.appengine.core.common.FakeDataProvider
import com.kglabs28.yapnplay.appengine.games.base.identity.SeatNumber
import com.kglabs28.yapnplay.appengine.games.base.play.Action
import com.kglabs28.yapnplay.appengine.games.base.play.ApplyResult
import com.kglabs28.yapnplay.appengine.games.base.play.DefaultGameRandom
import com.kglabs28.yapnplay.appengine.games.base.play.GameEvent
import com.kglabs28.yapnplay.appengine.games.elements.dice.mechanics.RollDice
import com.kglabs28.yapnplay.appengine.games.citybaazar.domain.CityBaazarColour
import com.kglabs28.yapnplay.appengine.games.citybaazar.domain.CityBaazarCommand
import com.kglabs28.yapnplay.appengine.games.citybaazar.domain.CityBaazarDebtInfo
import com.kglabs28.yapnplay.appengine.games.citybaazar.domain.CityBaazarEvent
import com.kglabs28.yapnplay.appengine.games.citybaazar.domain.CityBaazarMatch
import com.kglabs28.yapnplay.appengine.games.citybaazar.domain.CityBaazarPlayer
import com.kglabs28.yapnplay.appengine.games.citybaazar.domain.CityBaazarPresence
import com.kglabs28.yapnplay.appengine.games.citybaazar.domain.CityBaazarSpectator
import com.kglabs28.yapnplay.appengine.games.citybaazar.domain.CityBaazarTurnPhase
import com.kglabs28.yapnplay.appengine.games.citybaazar.domain.CityBaazarUpdate
import com.kglabs28.yapnplay.appengine.games.citybaazar.engine.BuildingBuilt
import com.kglabs28.yapnplay.appengine.games.citybaazar.engine.BuildingSold
import com.kglabs28.yapnplay.appengine.games.citybaazar.engine.CardDrawn
import com.kglabs28.yapnplay.appengine.games.citybaazar.engine.CityBaazarEconomy
import com.kglabs28.yapnplay.appengine.games.citybaazar.engine.CityBaazarEngine
import com.kglabs28.yapnplay.appengine.games.citybaazar.engine.CityBaazarPhase
import com.kglabs28.yapnplay.appengine.games.citybaazar.engine.CityBaazarRules
import com.kglabs28.yapnplay.appengine.games.citybaazar.engine.CityBaazarState
import com.kglabs28.yapnplay.appengine.games.citybaazar.engine.DiceRolled
import com.kglabs28.yapnplay.appengine.games.citybaazar.engine.MoneyMoved
import com.kglabs28.yapnplay.appengine.games.citybaazar.engine.SentToJail
import com.kglabs28.yapnplay.appengine.games.citybaazar.engine.TileBought
import com.kglabs28.yapnplay.appengine.games.citybaazar.engine.TileSold
import com.kglabs28.yapnplay.appengine.games.citybaazar.engine.TokenMoved
import com.kglabs28.yapnplay.appengine.games.citybaazar.engine.TurnPassed
import com.kglabs28.yapnplay.appengine.games.citybaazar.engine.TurnSkipped
import com.kglabs28.yapnplay.appengine.games.citybaazar.engine.WentBankrupt
import com.kglabs28.yapnplay.appengine.games.citybaazar.engine.action.BuildOnTile
import com.kglabs28.yapnplay.appengine.games.citybaazar.engine.action.BuyTile
import com.kglabs28.yapnplay.appengine.games.citybaazar.engine.action.DeclareBankruptcy
import com.kglabs28.yapnplay.appengine.games.citybaazar.engine.action.DeclineTile
import com.kglabs28.yapnplay.appengine.games.citybaazar.engine.action.PayJailFine
import com.kglabs28.yapnplay.appengine.games.citybaazar.engine.action.SellBuilding
import com.kglabs28.yapnplay.appengine.games.citybaazar.engine.action.SellTile
import com.kglabs28.yapnplay.appengine.games.citybaazar.engine.action.SkipBuild
import com.kglabs28.yapnplay.appengine.games.citybaazar.engine.action.UseJailPass
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.ROUND_LIMIT
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
import kotlin.math.abs
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

/**
 * In-memory stand-in for the server's City Baazar match actor: runs the
 * real [CityBaazarEngine] on the device, plays the other seats (the bot,
 * with human-like pauses), plays the user's turn when Autoplay is on or
 * they are away, plays it cautiously when their time runs out, and
 * streams every change with the events behind it. A room match seats six
 * with a spectator watching; Quick Play seats four, no spectators.
 * Deleted with the fakes.
 */
class FakeCityBaazarServer(
    private val clock: AppClock,
    dispatchers: DispatcherProvider,
) {
    private val scope = CoroutineScope(SupervisorJob() + dispatchers.default)
    private val engine = CityBaazarEngine()
    private val random = DefaultGameRandom()
    private val mutex = Mutex()
    private val fake = FakeDataProvider.CityBaazar

    private var matchId: String? = null
    private lateinit var state: CityBaazarState
    private var players: List<CityBaazarPlayer> = emptyList()
    private var spectators: List<FakeDataProvider.LudoMatch.Opponent> = emptyList()
    private var roomId: String? = null
    private var title = ""
    private var deadline: Instant? = null
    private var startedAt: Instant = Instant.DISTANT_PAST
    private var pauseMs = 0L
    private var loop: Job? = null
    private val myUserId = FakeDataProvider.Profile.me.userId

    private val updates = MutableSharedFlow<CityBaazarUpdate>(replay = 1, extraBufferCapacity = UPDATE_BUFFER)
    /** Bumped on every change, so the turn loop wakes up early. */
    private val version = MutableStateFlow(0L)

    fun match(id: String): Flow<CityBaazarUpdate> = flow {
        delay(FakeDataProvider.NETWORK_DELAY_MS)
        mutex.withLock { if (matchId != id) startMatch(id) }
        emitAll(updates)
    }

    /** Rotates "who is talking" among players and spectators with their mic on. */
    fun speaking(): Flow<Set<String>> = flow {
        var turn = 0
        while (true) {
            val talkers = players.filter { !it.isMe && it.micOn && it.presence == CityBaazarPresence.PLAYING }.map { it.userId } +
                spectators.filter { it.micOn }.map { it.userId }
            emit(talkers.getOrNull(turn % talkers.size.coerceAtLeast(1))?.let { setOf(it) }.orEmpty())
            turn++
            delay(FakeDataProvider.Lobby.SPEAKER_ROTATION_MS)
        }
    }

    suspend fun handle(command: CityBaazarCommand) = mutex.withLock {
        val me = players.firstOrNull { it.isMe } ?: return@withLock
        when (command) {
            CityBaazarCommand.Roll -> act(me.seat, RollDice)
            CityBaazarCommand.Buy -> act(me.seat, BuyTile)
            CityBaazarCommand.Decline -> act(me.seat, DeclineTile)
            is CityBaazarCommand.Build -> act(me.seat, BuildOnTile(command.tile))
            CityBaazarCommand.SkipBuild -> act(me.seat, SkipBuild)
            is CityBaazarCommand.SellBuilding -> act(me.seat, SellBuilding(command.tile))
            is CityBaazarCommand.SellTile -> act(me.seat, SellTile(command.tile))
            CityBaazarCommand.PayJailFine -> act(me.seat, PayJailFine)
            CityBaazarCommand.UseJailPass -> act(me.seat, UseJailPass)
            CityBaazarCommand.DeclareBankruptcy -> act(me.seat, DeclareBankruptcy)
            is CityBaazarCommand.SetAutoplay -> update(me.seat) { it.copy(autoplay = command.enabled) }
            is CityBaazarCommand.SetMic -> update(me.seat) { it.copy(micOn = command.on) }
            is CityBaazarCommand.SetAway -> update(me.seat) {
                it.copy(presence = if (command.away) CityBaazarPresence.AWAY else CityBaazarPresence.PLAYING)
            }
            // The user leaves; the bot plays their seat to the end.
            CityBaazarCommand.Leave -> update(me.seat) { it.copy(isMe = false, presence = CityBaazarPresence.BOT) }
        }
    }

    private fun startMatch(id: String) {
        loop?.cancel()
        matchId = id
        startedAt = clock.now()
        val room = FakeDataProvider.PlayRooms.myRooms.rooms.firstOrNull { id.contains(it.roomId) }
        roomId = room?.roomId
        title = room?.name ?: fake.QUICK_PLAY_TITLE
        // Spectators are a room feature; Quick Play has none.
        spectators = if (room != null) fake.spectators else emptyList()
        val others = fake.opponents.take((if (room != null) fake.ROOM_SEATS else fake.QUICK_PLAY_SEATS) - 1)
        state = engine.start(CityBaazarRules.STANDARD, others.size + 1, random) as CityBaazarState
        val meProfile = FakeDataProvider.Profile.me
        players = (1..others.size + 1).map { seat ->
            val colour = CityBaazarColour.entries[seat - 1]
            if (seat == MY_SEAT) {
                CityBaazarPlayer(seat, meProfile.userId, meProfile.displayName, meProfile.level, colour, isMe = true, micOn = false, autoplay = false)
            } else {
                val o = others[seat - 2]
                CityBaazarPlayer(seat, o.userId, o.name, o.level, colour, isMe = false, micOn = o.micOn, autoplay = false)
            }
        }
        pauseMs = 0
        deadline = null
        publish(emptyList())
        loop = scope.launch { turnLoop() }
    }

    /**
     * Plays whoever is up: other seats after a short "thinking" pause, the
     * user's seat with Autoplay on or while away, or cautiously when their
     * time runs out. Any change wakes it to look again.
     */
    private suspend fun turnLoop() {
        while (scope.isActive) {
            val (seat, waitMs, seen) = mutex.withLock {
                if (state.phase == CityBaazarPhase.FINISHED) return
                val player = players.first { it.seat == state.currentSeat }
                val untilDeadline = deadline?.let { (it - clock.now()).inWholeMilliseconds } ?: 0L
                Triple(state.currentSeat, if (botPlays(player)) pauseMs + fake.THINK_MS else untilDeadline, version.value)
            }
            val changed = withTimeoutOrNull(waitMs.coerceAtLeast(0).milliseconds) { version.first { it != seen } }
            if (changed != null) continue
            mutex.withLock {
                if (version.value == seen && state.currentSeat == seat && state.phase != CityBaazarPhase.FINISHED) {
                    val player = players.first { it.seat == seat }
                    val result = if (botPlays(player)) {
                        engine.autoplay(state, SeatNumber(seat), random)
                    } else {
                        engine.timeoutPlay(state, SeatNumber(seat), random)
                    }
                    applyResult(result)
                }
            }
        }
    }

    private fun botPlays(player: CityBaazarPlayer) = !player.isMe || player.autoplay || player.presence != CityBaazarPresence.PLAYING

    private fun act(seat: Int, action: Action) = applyResult(engine.apply(state, SeatNumber(seat), action, random))

    private fun applyResult(result: ApplyResult) {
        if (result !is ApplyResult.Success) return
        state = result.state as CityBaazarState
        publish(result.events)
    }

    private fun update(seat: Int, change: (CityBaazarPlayer) -> CityBaazarPlayer) {
        players = players.map { if (it.seat == seat) change(it) else it }
        publish(emptyList())
    }

    /** Sends the new snapshot; the next decision waits for its animations. */
    private fun publish(engineEvents: List<GameEvent>) {
        val events = engineEvents.mapNotNull { it.toEvent() }
        if (events.isNotEmpty()) pauseMs = events.sumOf { event ->
            when (event) {
                is CityBaazarEvent.Rolled -> fake.AFTER_ROLL_MS
                is CityBaazarEvent.Moved -> if (event.jump) fake.AFTER_MOVE_MS else fake.PER_STEP_MS * abs(event.steps) + fake.AFTER_MOVE_MS
                is CityBaazarEvent.Card -> fake.CARD_MS
                is CityBaazarEvent.Money -> fake.MONEY_MS
                else -> 0L
            }
        }
        if (events.isNotEmpty() || deadline == null) {
            deadline = clock.now() + pauseMs.milliseconds + engine.timing.turnSeconds.seconds
        }
        version.value++
        updates.tryEmit(CityBaazarUpdate(snapshot(), events))
    }

    private fun snapshot(): CityBaazarMatch {
        val finished = state.phase == CityBaazarPhase.FINISHED
        return CityBaazarMatch(
            matchId = matchId.orEmpty(),
            title = title,
            players = players.map { p ->
                p.copy(
                    cash = state.cashOf(p.seat),
                    position = state.positions[p.seat] ?: 0,
                    inJail = state.inJail(p.seat),
                    jailPasses = state.jailPasses[p.seat] ?: 0,
                    jailTries = state.jailTries[p.seat] ?: 0,
                    bankrupt = p.seat !in state.active,
                )
            },
            spectators = spectators.map { CityBaazarSpectator(it.userId, it.name, it.micOn) },
            spectatorCount = spectators.size,
            owners = state.owners,
            levels = state.levels,
            currentSeat = state.currentSeat,
            phase = when (state.phase) {
                CityBaazarPhase.ROLL -> CityBaazarTurnPhase.ROLL
                CityBaazarPhase.JAIL_CHOICE -> CityBaazarTurnPhase.JAIL
                CityBaazarPhase.BUY -> CityBaazarTurnPhase.BUY
                CityBaazarPhase.BUILD -> CityBaazarTurnPhase.BUILD
                CityBaazarPhase.DEBT -> CityBaazarTurnPhase.DEBT
                CityBaazarPhase.FINISHED -> CityBaazarTurnPhase.FINISHED
            },
            die1 = state.die1,
            die2 = state.die2,
            pendingTile = state.pendingTile,
            debt = state.debt?.let { CityBaazarDebtInfo(it.total, it.reason, it.tile) },
            buildable = if (state.phase == CityBaazarPhase.ROLL) CityBaazarEconomy.buildable(state, state.currentSeat) else emptyList(),
            turnEndsAt = if (finished) null else deadline,
            startedAt = startedAt,
            round = state.round,
            roundLimit = if (state.cbRules.house.roundLimit) ROUND_LIMIT else null,
            netWorth = players.associate { it.seat to CityBaazarEconomy.netWorth(state, it.seat) },
            ranking = if (finished) CityBaazarEngine.ranking(state) else emptyList(),
            roomId = roomId,
            viewerUserId = myUserId,
        )
    }

    private fun GameEvent.toEvent(): CityBaazarEvent? = when (this) {
        is DiceRolled -> CityBaazarEvent.Rolled(seat, die1, die2)
        is TokenMoved -> CityBaazarEvent.Moved(seat, from, to, steps, jump)
        is MoneyMoved -> CityBaazarEvent.Money(from, to, amount, reason, tile)
        is CardDrawn -> CityBaazarEvent.Card(seat, cardId)
        is TileBought -> CityBaazarEvent.Bought(seat, tile)
        is BuildingBuilt -> CityBaazarEvent.Built(seat, tile, level)
        is BuildingSold -> CityBaazarEvent.Sold(seat, tile)
        is TileSold -> CityBaazarEvent.Sold(seat, tile)
        is SentToJail -> CityBaazarEvent.Jailed(seat)
        is WentBankrupt -> CityBaazarEvent.Bankrupt(seat)
        is TurnSkipped -> CityBaazarEvent.Skipped(seat)
        is TurnPassed -> CityBaazarEvent.TurnPassed(seat)
        else -> null
    }

    private companion object {
        /** The fake always seats the user first. */
        const val MY_SEAT = 1
        const val UPDATE_BUFFER = 64
    }
}
