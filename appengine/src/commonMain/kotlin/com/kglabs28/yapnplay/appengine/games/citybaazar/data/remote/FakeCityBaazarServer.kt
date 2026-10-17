package com.kglabs28.yapnplay.appengine.games.citybaazar.data.remote

import com.kglabs28.yapnplay.appengine.appmanager.AppManager
import com.kglabs28.yapnplay.appengine.core.common.AppClock
import com.kglabs28.yapnplay.appengine.core.common.DispatcherProvider
import com.kglabs28.yapnplay.appengine.core.common.FakeDataProvider
import com.kglabs28.yapnplay.appengine.games.base.identity.SeatNumber
import com.kglabs28.yapnplay.appengine.games.base.play.Action
import com.kglabs28.yapnplay.appengine.games.base.play.ApplyResult
import com.kglabs28.yapnplay.appengine.games.base.play.DefaultGameRandom
import com.kglabs28.yapnplay.appengine.games.base.play.GameEvent
import com.kglabs28.yapnplay.appengine.games.elements.dice.mechanics.RollDice
import com.kglabs28.yapnplay.appengine.games.citybaazar.domain.CityBaazarAuctionInfo
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
import com.kglabs28.yapnplay.appengine.games.citybaazar.engine.AuctionEnded
import com.kglabs28.yapnplay.appengine.games.citybaazar.engine.AuctionStarted
import com.kglabs28.yapnplay.appengine.games.citybaazar.engine.BidPlaced
import com.kglabs28.yapnplay.appengine.games.citybaazar.engine.BuildingBuilt
import com.kglabs28.yapnplay.appengine.games.citybaazar.engine.BuildingSold
import com.kglabs28.yapnplay.appengine.games.citybaazar.engine.CardDrawn
import com.kglabs28.yapnplay.appengine.games.citybaazar.engine.CityBaazarEconomy
import com.kglabs28.yapnplay.appengine.games.citybaazar.engine.CityBaazarEngine
import com.kglabs28.yapnplay.appengine.games.citybaazar.engine.CityBaazarHouseRules
import com.kglabs28.yapnplay.appengine.games.citybaazar.engine.CityBaazarMandatoryRules
import com.kglabs28.yapnplay.appengine.games.citybaazar.engine.CityBaazarPhase
import com.kglabs28.yapnplay.appengine.games.citybaazar.engine.CityBaazarRules
import com.kglabs28.yapnplay.appengine.games.citybaazar.engine.CityBaazarState
import com.kglabs28.yapnplay.appengine.games.citybaazar.engine.DiceRolled
import com.kglabs28.yapnplay.appengine.games.citybaazar.engine.HalfRentTaken
import com.kglabs28.yapnplay.appengine.games.citybaazar.engine.LeftJail
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
import com.kglabs28.yapnplay.appengine.games.citybaazar.engine.action.CloseAuction
import com.kglabs28.yapnplay.appengine.games.citybaazar.engine.action.DeclareBankruptcy
import com.kglabs28.yapnplay.appengine.games.citybaazar.engine.action.DeclineTile
import com.kglabs28.yapnplay.appengine.games.citybaazar.engine.action.EndMatch
import com.kglabs28.yapnplay.appengine.games.citybaazar.engine.action.PayJailFine
import com.kglabs28.yapnplay.appengine.games.citybaazar.engine.action.PlaceBid
import com.kglabs28.yapnplay.appengine.games.citybaazar.engine.action.SellBuilding
import com.kglabs28.yapnplay.appengine.games.citybaazar.engine.action.SellTile
import com.kglabs28.yapnplay.appengine.games.citybaazar.engine.action.SkipBuild
import com.kglabs28.yapnplay.appengine.games.citybaazar.engine.action.StartAuction
import com.kglabs28.yapnplay.appengine.games.citybaazar.engine.action.StayInJail
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.AUCTION_SECONDS
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.BidSteps
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
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

/**
 * In-memory stand-in for the server's City Baazar match actor: runs the
 * real [CityBaazarEngine] on the device. CPU seats play the full bot (buy,
 * build, bid). The user's seat plays itself (roll and move only) with
 * Autoplay on or while away, and the same when their time runs out (with a
 * ₹50 fine for a missed roll). It runs auction timers (5 s after each bid,
 * CPU players bidding), ends the match when its time is up, and takes the
 * starting cash and limits from AppManager. A room match seats six with a
 * spectator; a random table seats four. Deleted with the fakes.
 */
class FakeCityBaazarServer(
    private val clock: AppClock,
    dispatchers: DispatcherProvider,
    private val appManager: AppManager,
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
    private var auctionEndsAt: Instant = Instant.DISTANT_PAST
    private var lastBidAt: Instant = Instant.DISTANT_PAST
    private var pauseMs = 0L
    private var loop: Job? = null
    private var ticker: Job? = null
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
            is CityBaazarCommand.StartAuction -> act(me.seat, StartAuction(command.tile))
            CityBaazarCommand.Bid -> act(me.seat, PlaceBid)
            CityBaazarCommand.PayJailFine -> act(me.seat, PayJailFine)
            CityBaazarCommand.StayInJail -> act(me.seat, StayInJail)
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
        ticker?.cancel()
        matchId = id
        startedAt = clock.now()
        val room = FakeDataProvider.PlayRooms.myRooms.rooms.firstOrNull { id.contains(it.roomId) }
        roomId = room?.roomId
        title = room?.name ?: fake.QUICK_PLAY_TITLE
        // Spectators are a room feature; random tables have none.
        spectators = if (room != null) fake.spectators else emptyList()
        val others = fake.opponents.take((if (room != null) fake.ROOM_SEATS else fake.QUICK_PLAY_SEATS) - 1)
        val rules = CityBaazarRules(
            mandatory = CityBaazarMandatoryRules(startingCash = appManager.cityBaazarStartingCash()),
            house = CityBaazarHouseRules(
                roundLimit = appManager.cityBaazarRoundLimit(),
                timeLimitMinutes = appManager.cityBaazarTimeLimitMinutes(),
            ),
        )
        state = engine.start(rules, others.size + 1, random) as CityBaazarState
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
        ticker = scope.launch { tickLoop() }
    }

    /**
     * Plays whoever is up: CPU seats after a short "thinking" pause, the
     * user's seat while Autoplay is on or they are away, or when their time
     * runs out. Waits while a property is being auctioned.
     */
    private suspend fun turnLoop() {
        while (scope.isActive) {
            val (seat, waitMs, seen) = mutex.withLock {
                if (state.phase == CityBaazarPhase.FINISHED) return
                if (state.phase == CityBaazarPhase.AUCTION) return@withLock Triple(state.currentSeat, fake.TICK_MS, version.value)
                val player = players.first { it.seat == state.currentSeat }
                val untilDeadline = deadline?.let { (it - clock.now()).inWholeMilliseconds } ?: 0L
                Triple(state.currentSeat, if (playsItself(player)) pauseMs + fake.THINK_MS else untilDeadline, version.value)
            }
            val changed = withTimeoutOrNull(waitMs.coerceAtLeast(0).milliseconds) { version.first { it != seen } }
            if (changed != null) continue
            mutex.withLock {
                if (version.value == seen && state.currentSeat == seat && state.phase != CityBaazarPhase.FINISHED && state.phase != CityBaazarPhase.AUCTION) {
                    val player = players.first { it.seat == seat }
                    val result = when {
                        !player.isMe -> engine.autoplay(state, SeatNumber(seat), random)
                        playsItself(player) -> engine.cautiousPlay(state, SeatNumber(seat), random, lateFine = false)
                        // Time ran out on the user: roll and move for them (₹50 for a missed roll).
                        else -> engine.cautiousPlay(state, SeatNumber(seat), random, lateFine = true)
                    }
                    applyResult(result)
                }
            }
        }
    }

    /** Auction timers and CPU bids; the match time limit. */
    private suspend fun tickLoop() {
        while (scope.isActive) {
            delay(fake.TICK_MS)
            mutex.withLock {
                if (state.phase == CityBaazarPhase.FINISHED) return
                val now = clock.now()
                state.cbRules.house.timeLimitMinutes?.let { limit ->
                    if (now - startedAt >= limit.minutes) return@withLock act(state.currentSeat, EndMatch)
                }
                val auction = state.auction ?: return@withLock
                if (now >= auctionEndsAt) return@withLock act(state.currentSeat, CloseAuction)
                if (now - lastBidAt < fake.CPU_BID_DELAY_MS.milliseconds) return@withLock
                players.filter { !it.isMe && it.seat != auction.seller && engine.bot.wantsToBid(state, it.seat) }
                    .shuffled()
                    .firstOrNull { Random.nextDouble() < fake.CPU_BID_CHANCE }
                    ?.let { act(it.seat, PlaceBid) }
            }
        }
    }

    private fun playsItself(player: CityBaazarPlayer) = !player.isMe || player.autoplay || player.presence != CityBaazarPresence.PLAYING

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
        val now = clock.now()
        if (events.any { it is CityBaazarEvent.AuctionStarted || it is CityBaazarEvent.BidPlaced }) {
            auctionEndsAt = now + AUCTION_SECONDS.seconds
            lastBidAt = now
        }
        val moves = events.filterNot { it is CityBaazarEvent.BidPlaced || it is CityBaazarEvent.AuctionStarted }
        if (moves.isNotEmpty()) pauseMs = moves.sumOf { event ->
            when (event) {
                is CityBaazarEvent.Rolled -> fake.AFTER_ROLL_MS
                is CityBaazarEvent.Moved -> if (event.jump) fake.AFTER_MOVE_MS else fake.PER_STEP_MS * abs(event.steps) + fake.AFTER_MOVE_MS
                is CityBaazarEvent.Card -> fake.CARD_MS
                is CityBaazarEvent.Money -> fake.MONEY_MS
                else -> 0L
            }
        }
        if (moves.isNotEmpty() || deadline == null) {
            deadline = now + pauseMs.milliseconds + engine.timing.turnSeconds.seconds
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
                    halfRent = p.seat in state.halfRent,
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
                CityBaazarPhase.JAIL_ROLL -> CityBaazarTurnPhase.JAIL_ROLL
                CityBaazarPhase.JAIL_CHOICE -> CityBaazarTurnPhase.JAIL_CHOICE
                CityBaazarPhase.BUY -> CityBaazarTurnPhase.BUY
                CityBaazarPhase.BUILD -> CityBaazarTurnPhase.BUILD
                CityBaazarPhase.DEBT -> CityBaazarTurnPhase.DEBT
                CityBaazarPhase.AUCTION -> CityBaazarTurnPhase.AUCTION
                CityBaazarPhase.FINISHED -> CityBaazarTurnPhase.FINISHED
            },
            die1 = state.die1,
            die2 = state.die2,
            pendingTile = state.pendingTile,
            debt = state.debt?.let { CityBaazarDebtInfo(it.total, it.reason, it.tile) },
            jailTriesLeft = state.jailTriesLeft,
            auction = state.auction?.let {
                CityBaazarAuctionInfo(it.tile, it.seller, it.bid, it.leader, if (it.leader == null) it.bid else it.bid + BidSteps.after(it.bid), auctionEndsAt)
            },
            turnEndsAt = if (finished) null else deadline,
            startedAt = startedAt,
            timeEndsAt = state.cbRules.house.timeLimitMinutes?.let { startedAt + it.minutes },
            round = state.round,
            roundLimit = state.cbRules.house.roundLimit,
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
        is LeftJail -> CityBaazarEvent.Freed(seat)
        is HalfRentTaken -> CityBaazarEvent.HalfRent(seat)
        is AuctionStarted -> CityBaazarEvent.AuctionStarted(seller, tile, opening)
        is BidPlaced -> CityBaazarEvent.BidPlaced(seat, tile, amount)
        is AuctionEnded -> CityBaazarEvent.AuctionEnded(seller, tile, winner, amount)
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
