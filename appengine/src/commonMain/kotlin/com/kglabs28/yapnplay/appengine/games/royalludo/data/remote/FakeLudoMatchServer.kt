package com.kglabs28.yapnplay.appengine.games.royalludo.data.remote

import com.kglabs28.yapnplay.appengine.core.common.AppClock
import com.kglabs28.yapnplay.appengine.core.common.DispatcherProvider
import com.kglabs28.yapnplay.appengine.core.common.FakeDataProvider
import com.kglabs28.yapnplay.appengine.games.royalludo.domain.LudoColour
import com.kglabs28.yapnplay.appengine.games.royalludo.domain.LudoCommand
import com.kglabs28.yapnplay.appengine.games.royalludo.domain.LudoEvent
import com.kglabs28.yapnplay.appengine.features.seathandover.domain.PresentMember
import com.kglabs28.yapnplay.appengine.features.seathandover.domain.SeatHandoverRules
import com.kglabs28.yapnplay.appengine.features.seathandover.domain.SeatOffer
import com.kglabs28.yapnplay.appengine.features.seathandover.domain.SeatOfferStage
import com.kglabs28.yapnplay.appengine.games.royalludo.domain.LudoMatch
import com.kglabs28.yapnplay.appengine.games.royalludo.domain.LudoPhase
import com.kglabs28.yapnplay.appengine.games.royalludo.domain.LudoPlayer
import com.kglabs28.yapnplay.appengine.games.royalludo.domain.LudoPresence
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
 * that caused it. A player who leaves the app is played for at once. In
 * a room match a seat changes hands by [SeatHandoverRules] (the match
 * waits while someone decides); Quick Play has no spectators, so the bot
 * takes over. A demo runs once per match (see FakeDataProvider).
 * Deleted with the fakes.
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
    private var presenceLoop: Job? = null
    private var spectators = fake.spectators
    private val awaySince = mutableMapOf<Int, Instant>()
    private val missedTurns = mutableMapOf<Int, Int>()
    private var roomId: String? = null
    private var roomLeaderId: String? = null
    private var seatOffer: SeatOffer? = null
    private var offerStartedAt: Instant = Instant.DISTANT_PAST
    private val declined = mutableSetOf<String>()
    private var demoDone = false
    /** The live room match: the user only watches (a spectator plays in their place). */
    private var watchingMatch = false
    /** Whole turns in a row the bot played for a seat on timeout. */
    private val idleTurns = mutableMapOf<Int, Int>()
    private var turnOwner = 0
    private var turnTimedOut = false
    private var turnActed = false
    private val myUserId = FakeDataProvider.Profile.me.userId

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
            // Players and spectators with their mic on take turns talking.
            val talkers = players.filter { !it.isMe && it.micOn && !it.removed && it.presence == LudoPresence.PLAYING }.map { it.userId } +
                spectators.filter { it.micOn && it.userId != myUserId }.map { it.userId }
            emit(talkers.getOrNull(turn % talkers.size.coerceAtLeast(1))?.let { setOf(it) }.orEmpty())
            turn++
            delay(FakeDataProvider.Lobby.SPEAKER_ROTATION_MS)
        }
    }

    suspend fun handle(command: LudoCommand) = mutex.withLock {
        val me = players.firstOrNull { it.isMe } ?: return@withLock spectatorCommand(command)
        when (command) {
            LudoCommand.Roll ->
                if (!onHold() && state.currentSeat == me.seat && state.phase == Phase.AWAITING_ROLL) {
                    turnActed = true
                    clearAbsenceCheck(me.seat)
                    apply(me.seat, RollDice)
                }
            is LudoCommand.Move ->
                if (!onHold() && state.currentSeat == me.seat && state.phase == Phase.AWAITING_MOVE) {
                    turnActed = true
                    clearAbsenceCheck(me.seat)
                    apply(me.seat, MovePiece(PieceId(command.piece.toString())))
                }
            is LudoCommand.SetAutoplay -> {
                // Autoplay off on a seat the bot took (nobody else did): it is theirs again.
                players = players.map {
                    if (!it.isMe) it
                    else if (!command.enabled && it.presence == LudoPresence.BOT) it.copy(autoplay = false, presence = LudoPresence.PLAYING)
                    else it.copy(autoplay = command.enabled)
                }
                idleTurns.remove(me.seat)
                publish(emptyList())
            }
            is LudoCommand.SetMic -> {
                players = players.map { if (it.isMe) it.copy(micOn = command.on) else it }
                publish(emptyList())
            }
            is LudoCommand.SetAway -> setAway(me.seat, command.away)
            is LudoCommand.AnswerSeatOffer -> answerOffer(command.accept)
            LudoCommand.StillHere -> stillHere()
            is LudoCommand.ChooseSeatTaker -> leaderChoice(command.giveToUserId)
            // The fake user is always seated; spectators take seats on the real server.
            is LudoCommand.TakeSeat -> Unit
            // The seat goes to the bot or the picked spectator; the user stays on as a spectator.
            is LudoCommand.Leave -> leave(me, command.giveToUserId)
        }
    }

    private fun startMatch(id: String) {
        loop?.cancel()
        presenceLoop?.cancel()
        val room = FakeDataProvider.PlayRooms.myRooms.rooms.firstOrNull { id.contains(it.roomId) }
        roomId = room?.roomId
        roomLeaderId = room?.let { if (it.isLeader) myUserId else fake.OTHER_LEADER_ID }
        // Spectators are a room feature; Quick Play has none.
        spectators = if (room != null) fake.spectators else emptyList()
        awaySince.clear()
        missedTurns.clear()
        idleTurns.clear()
        turnTimedOut = false
        turnActed = false
        seatOffer = null
        declined.clear()
        demoDone = false
        matchId = id
        startedAt = clock.now()
        state = engine.start(RoyalLudoRules.TRADITIONAL, ROYAL_LUDO_PLAYERS, random) as RoyalLudoState
        val meProfile = FakeDataProvider.Profile.me
        watchingMatch = id == FakeDataProvider.Lobby.LIVE_MATCH_ID
        // Watching: the room's players plus the first spectator hold the seats.
        val seated = if (watchingMatch) fake.opponents + fake.spectators.first() else null
        players = (1..ROYAL_LUDO_PLAYERS).map { seat ->
            val colour = LudoColour.entries[state.colourForSeat(seat)]
            if (seated == null && seat == MY_SEAT) {
                LudoPlayer(seat, meProfile.userId, meProfile.displayName, meProfile.level, colour, isMe = true, micOn = false, autoplay = false, removed = false, avatarId = FakeDataProvider.Profile.avatarOf(meProfile.userId))
            } else {
                val o = seated?.get(seat - 1) ?: fake.opponents[seat - 2]
                LudoPlayer(seat, o.userId, o.name, o.level, colour, isMe = false, micOn = o.micOn, autoplay = false, removed = false, avatarId = FakeDataProvider.Profile.avatarOf(o.userId))
            }
        }
        if (watchingMatch) {
            // People join a running match at any stage: it may be starting, midway or nearly over.
            var plays = 0
            repeat(fake.WATCH_STAGE_PLAYS.random()) {
                if (state.phase == Phase.FINISHED) return@repeat
                val result = engine.autoplay(state, SeatNumber(state.currentSeat), random) as? ApplyResult.Success ?: return@repeat
                state = result.state as RoyalLudoState
                plays++
            }
            startedAt = clock.now() - (plays * fake.WATCH_SECONDS_PER_PLAY).seconds
            spectators = fake.spectators.drop(1) +
                FakeDataProvider.LudoMatch.Opponent(meProfile.userId, meProfile.displayName, meProfile.level, micOn = false, onlineMinutes = fake.MY_ONLINE_MINUTES)
        }
        pauseMs = 0
        publish(emptyList())
        loop = scope.launch { turnLoop() }
        presenceLoop = scope.launch { presenceLoop() }
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
                // On hold while a seat changes hands: nobody plays.
                if (onHold()) return@withLock Triple(state.currentSeat, fake.PRESENCE_TICK_MS, version.value)
                val auto = !player.isMe || player.autoplay || player.presence != LudoPresence.PLAYING
                val untilDeadline = deadline?.let { (it - clock.now()).inWholeMilliseconds } ?: 0L
                Triple(state.currentSeat, if (auto) pauseMs + fake.THINK_MS else untilDeadline, version.value)
            }
            val changed = withTimeoutOrNull(waitMs.coerceAtLeast(0).milliseconds) { version.first { it != seen } }
            if (changed != null) continue
            mutex.withLock {
                if (version.value == seen && !onHold() && state.currentSeat == seat && state.phase != Phase.FINISHED) {
                    if (state.phase == Phase.AWAITING_ROLL && players.first { it.seat == seat }.presence == LudoPresence.AWAY) {
                        missedTurns[seat] = (missedTurns[seat] ?: 0) + 1
                    }
                    val owner = players.first { it.seat == seat }
                    // The turn ran out on someone playing for themselves (not Autoplay).
                    if (owner.isMe && !owner.autoplay && owner.presence == LudoPresence.PLAYING) turnTimedOut = true
                    applyResult(engine.autoplay(state, SeatNumber(seat), random))
                }
            }
        }
    }

    /** Away: the server plays for them. Back: the seat is theirs again, unless someone took it. */
    private fun setAway(seat: Int, away: Boolean) {
        val player = players.firstOrNull { it.seat == seat } ?: return
        if (player.removed) return
        if (away) {
            if (player.presence != LudoPresence.PLAYING) return
            awaySince[seat] = clock.now()
            missedTurns[seat] = 0
            setPresence(seat, LudoPresence.AWAY)
        } else {
            if (player.presence == LudoPresence.PLAYING) return
            awaySince.remove(seat)
            missedTurns.remove(seat)
            if (seatOffer?.seat == seat && seatOffer?.stage == SeatOfferStage.OpenToAll) seatOffer = null
            setPresence(seat, LudoPresence.PLAYING)
        }
        publish(emptyList())
    }

    private fun setPresence(seat: Int, presence: LudoPresence) {
        players = players.map { if (it.seat == seat) it.copy(presence = presence) else it }
    }

    private fun onHold() = seatOffer?.stage?.holdsMatch == true

    /**
     * A whole turn ended (extra rolls in it count once). A turn the bot
     * played on timeout with no action from the owner counts as idle;
     * after [SeatHandoverRules.IDLE_TURNS_BEFORE_CHECK] in a row they are
     * asked whether they are still there.
     */
    private fun closeTurn() {
        val seat = turnOwner
        if (turnTimedOut && !turnActed) {
            val idle = (idleTurns[seat] ?: 0) + 1
            idleTurns[seat] = idle
            val owner = players.firstOrNull { it.seat == seat }
            if (idle >= SeatHandoverRules.IDLE_TURNS_BEFORE_CHECK && seatOffer == null && owner != null && owner.presence == LudoPresence.PLAYING) {
                val now = clock.now()
                seatOffer = SeatOffer(seat, owner.name, SeatOfferStage.ConfirmingAbsence(owner.userId, owner.name), now + SeatHandoverRules.ABSENCE_CHECK_SECONDS.seconds)
                offerStartedAt = now
            }
        } else {
            idleTurns.remove(seat)
        }
        turnOwner = state.currentSeat
        turnTimedOut = false
        turnActed = false
    }

    /** Watching (no seat): the mic, and answers about a seat. */
    private fun spectatorCommand(command: LudoCommand) {
        when (command) {
            is LudoCommand.SetMic -> {
                spectators = spectators.map { if (it.userId == myUserId) it.copy(micOn = command.on) else it }
                publish(emptyList())
            }
            is LudoCommand.AnswerSeatOffer -> answerOffer(command.accept)
            is LudoCommand.ChooseSeatTaker -> leaderChoice(command.giveToUserId)
            is LudoCommand.TakeSeat -> if (seatOffer?.seat == command.seat && seatOffer?.stage == SeatOfferStage.OpenToAll) standIn(command.seat, myUserId)
            else -> Unit
        }
    }

    /**
     * The user gives up their seat: the bot takes it, or (in a room) the
     * picked spectator is offered it. The user keeps watching, as a
     * spectator in a room.
     */
    private fun leave(me: LudoPlayer, giveTo: String?) {
        players = players.map { if (it.isMe) it.copy(isMe = false, autoplay = false, presence = LudoPresence.AWAY) else it }
        val pick = spectators.firstOrNull { it.userId == giveTo }
        if (roomId != null) {
            spectators = spectators + FakeDataProvider.LudoMatch.Opponent(me.userId, me.name, me.level, me.micOn, fake.MY_ONLINE_MINUTES)
        }
        idleTurns.remove(me.seat)
        if (pick != null && roomId != null) offerTo(me.seat, me.name, pick.userId, pick.name, round = 1, now = clock.now()) else toBot(me.seat)
    }

    /** Playing again during the "still here?" check answers it. */
    private fun clearAbsenceCheck(seat: Int) {
        val offer = seatOffer ?: return
        if (offer.seat != seat || offer.stage !is SeatOfferStage.ConfirmingAbsence) return
        seatOffer = null
        idleTurns.remove(seat)
    }

    /** "I'm still here": the check ends, the seat stays theirs. */
    private fun stillHere() {
        val offer = seatOffer ?: return
        val stage = offer.stage as? SeatOfferStage.ConfirmingAbsence ?: return
        if (stage.userId != myUserId) return
        idleTurns.remove(offer.seat)
        seatOffer = null
        endHold()
        publish(emptyList())
    }

    /** Once a second: the demo, seats of people away too long, and any handover in progress. */
    private suspend fun presenceLoop() {
        while (scope.isActive) {
            delay(fake.PRESENCE_TICK_MS)
            mutex.withLock {
                if (state.phase == Phase.FINISHED) return
                val now = clock.now()
                runDemo(now)
                val offer = seatOffer
                if (offer == null) handleAwaySeats(now) else advance(offer, now)
            }
        }
    }

    /** Once per match: in a room an opponent leaves and offers their seat to a spectator; in Quick Play one goes away. */
    private fun runDemo(now: Instant) {
        val after = if (watchingMatch) fake.WATCH_DEMO_AFTER_MS else fake.DEMO_AFTER_MS
        if (demoDone || now - startedAt < after.milliseconds) return
        demoDone = true
        val leaver = players.lastOrNull { !it.isMe && !it.removed && it.presence == LudoPresence.PLAYING } ?: return
        // Watching: the seat is offered to the user.
        val pick = if (watchingMatch) spectators.firstOrNull { it.userId == myUserId } else spectators.firstOrNull()
        if (roomId != null && pick != null) {
            setPresence(leaver.seat, LudoPresence.AWAY)
            offerTo(leaver.seat, leaver.name, pick.userId, pick.name, round = 1, now = now)
        } else {
            setAway(leaver.seat, away = true)
        }
    }

    /** Away too long: in a room the seat is open to every spectator; in Quick Play the bot takes it. */
    private fun handleAwaySeats(now: Instant) {
        val p = players.firstOrNull { p ->
            p.presence == LudoPresence.AWAY && !p.removed && (
                now - (awaySince[p.seat] ?: now) >= fake.OPEN_SEAT_AFTER_MS.milliseconds ||
                    (missedTurns[p.seat] ?: 0) >= fake.OPEN_SEAT_AFTER_MISSED_TURNS
                )
        } ?: return
        if (roomId != null && spectators.isNotEmpty()) {
            seatOffer = SeatOffer(p.seat, p.name, SeatOfferStage.OpenToAll, now + fake.SEAT_CLAIM_MS.milliseconds)
            offerStartedAt = now
            publish(emptyList())
        } else {
            toBot(p.seat)
        }
    }

    /** Moves a handover on: fake answers (the user's own come in as commands), then timeouts. */
    private fun advance(offer: SeatOffer, now: Instant) {
        val elapsed = now - offerStartedAt
        when (val stage = offer.stage) {
            is SeatOfferStage.ConfirmingAbsence -> if (now >= offer.endsAt) {
                idleTurns.remove(offer.seat)
                askLeader(offer, now)
            }
            is SeatOfferStage.ToSpectator -> when {
                // Demo: the leaver's pick declines; the leader's pick accepts.
                stage.userId != myUserId && elapsed >= fake.NOMINEE_ANSWER_MS.milliseconds ->
                    if (stage.round == 1) passed(offer, stage, now) else standIn(offer.seat, stage.userId)
                now >= offer.endsAt -> passed(offer, stage, now)
            }
            is SeatOfferStage.LeaderChoosing -> when {
                stage.leaderUserId != myUserId && elapsed >= fake.LEADER_ANSWER_MS.milliseconds ->
                    leaderChoice(spectators.firstOrNull { it.userId !in declined }?.userId)
                now >= offer.endsAt -> toBot(offer.seat)
            }
            SeatOfferStage.OpenToAll -> when {
                offer.seat != MY_SEAT && spectators.isNotEmpty() && elapsed >= fake.STAND_IN_AFTER_MS.milliseconds ->
                    standIn(offer.seat, spectators.first().userId)
                now >= offer.endsAt -> toBot(offer.seat)
            }
        }
    }

    private fun offerTo(seat: Int, leaverName: String, userId: String, name: String, round: Int, now: Instant) {
        seatOffer = SeatOffer(seat, leaverName, SeatOfferStage.ToSpectator(userId, name, round), now + SeatHandoverRules.SPECTATOR_ANSWER_SECONDS.seconds)
        offerStartedAt = now
        publish(emptyList())
    }

    /** The named spectator declined or let it run out: first time the leader decides, second time the bot. */
    private fun passed(offer: SeatOffer, stage: SeatOfferStage.ToSpectator, now: Instant) {
        declined += stage.userId
        if (stage.round > 1) return toBot(offer.seat)
        askLeader(offer, now)
    }

    /** The leader (or whoever stands in for them) picks; in Quick Play, or with nobody to pick, the bot takes it. */
    private fun askLeader(offer: SeatOffer, now: Instant) {
        if (roomId == null || spectators.isEmpty()) return toBot(offer.seat)
        val present = players.filter { !it.removed && it.seat != offer.seat }.map { PresentMember(it.userId, it.name, onlineSince(it.userId)) } +
            spectators.map { PresentMember(it.userId, it.name, onlineSince(it.userId)) }
        val decider = SeatHandoverRules.decider(roomLeaderId, present) ?: return toBot(offer.seat)
        seatOffer = offer.copy(
            stage = SeatOfferStage.LeaderChoosing(decider.userId, decider.name),
            endsAt = now + SeatHandoverRules.LEADER_CHOICE_SECONDS.seconds,
        )
        offerStartedAt = now
        publish(emptyList())
    }

    /** The user, offered a seat by name, answers. (Never in the fake: the user is always seated.) */
    private fun answerOffer(accept: Boolean) {
        val offer = seatOffer ?: return
        val stage = offer.stage as? SeatOfferStage.ToSpectator ?: return
        if (stage.userId != myUserId) return
        if (accept) standIn(offer.seat, stage.userId) else passed(offer, stage, clock.now())
    }

    /** The leader's pick: a spectator gets the same window, the bot (null) takes it at once. */
    private fun leaderChoice(giveTo: String?) {
        val offer = seatOffer ?: return
        if (offer.stage !is SeatOfferStage.LeaderChoosing) return
        val pick = spectators.firstOrNull { it.userId == giveTo } ?: return toBot(offer.seat)
        offerTo(offer.seat, offer.leaverName, pick.userId, pick.name, round = 2, now = clock.now())
    }

    private fun toBot(seat: Int) {
        seatOffer = null
        setPresence(seat, LudoPresence.BOT)
        endHold()
        publish(emptyList())
    }

    /** A spectator takes [seat]: same colour and pieces, their name and voice. */
    private fun standIn(seat: Int, userId: String) {
        val joiner = spectators.firstOrNull { it.userId == userId } ?: return toBot(seat)
        spectators = spectators.filterNot { it.userId == userId }
        val replaced = players.first { it.seat == seat }
        // The user, replaced, stays on and is listed as a spectator.
        if (replaced.isMe && roomId != null) {
            spectators = spectators + FakeDataProvider.LudoMatch.Opponent(replaced.userId, replaced.name, replaced.level, replaced.micOn, fake.MY_ONLINE_MINUTES)
        }
        players = players.map {
            if (it.seat == seat) {
                // If it was the user's seat, they now watch as a spectator.
                it.copy(userId = joiner.userId, name = joiner.name, level = joiner.level, isMe = joiner.userId == myUserId, micOn = joiner.micOn, autoplay = false, presence = LudoPresence.PLAYING, avatarId = FakeDataProvider.Profile.avatarOf(joiner.userId))
            } else {
                it
            }
        }
        awaySince.remove(seat)
        missedTurns.remove(seat)
        seatOffer = null
        endHold()
        publish(emptyList())
    }

    /** Play resumes with a fresh turn timer. */
    private fun endHold() {
        deadline = clock.now() + engine.timing.turnSeconds.seconds
    }

    private fun onlineSince(userId: String): Long {
        val minutes = if (userId == myUserId) fake.MY_ONLINE_MINUTES else (fake.opponents + fake.spectators).firstOrNull { it.userId == userId }?.onlineMinutes ?: 0L
        return startedAt.toEpochMilliseconds() - minutes * MILLIS_PER_MINUTE
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
        if (state.currentSeat != turnOwner) closeTurn()
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
            spectators = spectators.map { LudoSpectator(it.userId, it.name, it.micOn) },
            spectatorCount = spectators.size,
            pieces = state.pieces,
            currentSeat = state.currentSeat,
            phase = when (state.phase) {
                Phase.AWAITING_ROLL -> LudoPhase.ROLL
                Phase.AWAITING_MOVE -> LudoPhase.MOVE
                Phase.FINISHED -> LudoPhase.FINISHED
            },
            lastRoll = state.lastRoll,
            legalPieces = state.legalPieces,
            turnEndsAt = if (finished || onHold()) null else deadline,
            startedAt = startedAt,
            finishedSeats = state.finishingOrder,
            ranking = if (finished) engine.outcome(state).ranking.sortedBy { it.place }.map { it.seat.value } else emptyList(),
            seatOffer = if (finished) null else seatOffer,
            roomId = roomId,
            viewerUserId = myUserId,
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
        const val MILLIS_PER_MINUTE = 60_000L
    }
}
