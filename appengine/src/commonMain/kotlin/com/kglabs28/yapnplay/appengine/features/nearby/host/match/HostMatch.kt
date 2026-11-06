package com.kglabs28.yapnplay.appengine.features.nearby.host.match

import com.kglabs28.yapnplay.appengine.features.nearby.host.HostClock
import com.kglabs28.yapnplay.appengine.features.nearby.host.HostConstants
import com.kglabs28.yapnplay.appengine.features.nearby.host.HostEnvelopes
import com.kglabs28.yapnplay.appengine.features.nearby.host.HostRefusal
import com.kglabs28.yapnplay.appengine.features.nearby.host.games.BotMode
import com.kglabs28.yapnplay.appengine.features.nearby.host.settings.GameSettings
import com.kglabs28.yapnplay.appengine.features.nearby.host.settings.Settings
import com.kglabs28.yapnplay.appengine.games.base.play.GameEvent
import com.kglabs28.yapnplay.appengine.protocol.ErrorCode
import com.kglabs28.yapnplay.appengine.protocol.MatchEnded
import com.kglabs28.yapnplay.appengine.protocol.MatchSeatStatus
import com.kglabs28.yapnplay.appengine.protocol.MatchSnapshot
import com.kglabs28.yapnplay.appengine.protocol.RankedSeat
import com.kglabs28.yapnplay.appengine.protocol.SeatPresence
import com.kglabs28.yapnplay.appengine.protocol.transport.Channel
import com.kglabs28.yapnplay.appengine.protocol.transport.Envelope
import com.kglabs28.yapnplay.appengine.protocol.transport.MessageType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

/**
 * One match the phone hosts for a Nearby table: the referee, like the
 * backend's match actor. It rolls, validates, plays CPUs and timed-out
 * turns, runs the keep-playing and end-game votes, and sends every viewer
 * their own snapshot after each change. Nothing is paid or saved: offline
 * play earns no coins or XP. One request or timer at a time ([lock]).
 */
class HostMatch(
    private val state: HostMatchState,
    private val clock: HostClock,
    private val settings: Settings,
    private val envelopes: HostEnvelopes,
    private val scope: CoroutineScope,
    /** Sends [Envelope] to one person at the table (the host's own app or a guest's phone). */
    private val deliver: (userId: String, envelope: Envelope) -> Unit,
    /** The match is over (its result went out). */
    private val onFinished: (matchId: String) -> Unit,
    /** Something changed (the next host's copy is due). */
    private val onChanged: () -> Unit = {},
) {
    val matchId: String get() = state.matchId
    val gameId: String get() = state.gameId
    val roomId: String get() = state.roomId
    val isFinished: Boolean get() = state.ended

    private val lock = Mutex()
    private val snapshots = HostSnapshotBuilder(state)
    private var wakeToken = 0L
    private val ticker: Job = scope.launch {
        while (isActive) {
            delay(HostConstants.MATCH_TICK_MS)
            lock.withLock { runCatching { tick() } }
        }
    }

    /** Opens the table: play waits for every human to open it (up to the start wait). */
    suspend fun open(humans: Set<String>) = lock.withLock {
        val now = clock.nowMillis()
        if (humans.isNotEmpty()) {
            state.waitingFor += humans
            state.startHoldUntil = now + settings[GameSettings.START_WAIT_SECONDS] * HostConstants.MILLIS_PER_SECOND
        }
        publish(emptyList())
    }

    fun stop() {
        ticker.cancel()
    }

    /** The whole match, for the phone that would host it next. Null once it is over. */
    suspend fun save(): HostMatchSave? = lock.withLock {
        if (state.ended) return@withLock null
        HostMatchSave(
            matchId = state.matchId,
            gameId = state.gameId,
            roomId = state.roomId,
            title = state.title,
            startedAt = state.startedAt,
            seats = state.seats.toList(),
            spectators = state.spectators.mapValues { (_, who) -> listOf(who.first, who.second) },
            version = state.version,
            deadline = state.deadline,
            pauseMs = state.pauseMs,
            botWakeAt = state.botWakeAt,
            turnOwner = state.turnOwner,
            extensionVote = state.extensionVote?.let { HostVoteSave(it.voters, it.endsAt, it.yes.toSet(), it.no.toSet()) },
            endVote = state.endVote?.let { HostVoteSave(it.voters, it.endsAt, it.yes.toSet(), it.no.toSet(), it.proposerSeat, it.startedAt) },
            lastEndProposal = state.lastEndProposal.toMap(),
            waitingFor = state.waitingFor.toSet(),
            startHoldUntil = state.startHoldUntil,
            session = state.session.save(),
        )
    }

    /** Taken over from a host who left: the turn timers run again and everyone gets the table. */
    suspend fun resume() = lock.withLock {
        scheduleWake()
        publish(emptyList())
    }

    // ---------------- requests ----------------

    /** A snapshot for a player (they opened the table), or start watching for anyone else at the party. */
    suspend fun snapshotFor(userId: String, displayName: String, avatarId: String): MatchSnapshot = lock.withLock {
        val seat = state.seatOf(userId)
        if (seat != null) {
            if (!seat.connected || seat.presence == SeatPresence.AWAY && seat.disconnectedAt != null) {
                state.update(seat.seat) { copy(connected = true, disconnectedAt = null, presence = SeatPresence.PLAYING) }
                publish(emptyList())
            }
            if (state.waitingFor.remove(userId) && state.waitingFor.isEmpty()) endStartHold()
        } else if (userId !in state.spectators) {
            if (state.ended) throw HostRefusal(ErrorCode.NOT_FOUND, MATCH_OVER)
            state.spectators[userId] = displayName to avatarId
            publish(emptyList())
        }
        snapshots.build(userId, clock.nowMillis(), emptyList())
    }

    /** A game move ([type] and its raw payload, decoded by the game). */
    suspend fun play(userId: String, type: String, payload: JsonObject) = act {
        val seat = state.seatOf(userId) ?: throw HostRefusal(ErrorCode.NOT_YOUR_TURN, NOT_SEATED)
        if (state.onHold) throw HostRefusal(ErrorCode.NOT_YOUR_TURN, ON_HOLD)
        val session = state.session
        val move = session.decode(type, payload) ?: throw HostRefusal(ErrorCode.BAD_REQUEST, UNKNOWN_ACTION)
        if (seat.seat != session.currentSeat && !session.waitsForTick && !session.actsOutOfTurn(seat.seat, move)) {
            throw HostRefusal(ErrorCode.NOT_YOUR_TURN, NOT_YOUR_TURN)
        }
        val events = session.apply(seat.seat, move) ?: throw HostRefusal(ErrorCode.ILLEGAL_MOVE, ILLEGAL_MOVE)
        // Acting means they are at the table.
        if (seat.presence == SeatPresence.AWAY) state.update(seat.seat) { copy(presence = SeatPresence.PLAYING) }
        publish(events, outOfTurn = seat.seat != state.turnOwner)
    }

    /** A side message (a Doodle King stroke): the game keeps it and it goes straight on, with no full update. */
    suspend fun relay(userId: String, type: String, payload: JsonObject) = lock.withLock {
        val seat = state.seatOf(userId)?.seat
        if (seat == null && userId !in state.spectators) throw HostRefusal(ErrorCode.NOT_MEMBER, NOT_SEATED)
        val out = state.session.relay(seat, type, payload) ?: throw HostRefusal(ErrorCode.ILLEGAL_MOVE, ILLEGAL_MOVE)
        out.toOthers?.let { message ->
            val push = envelopes.push(Channel.MATCH, MessageType.MATCH_RELAY, JsonObject.serializer(), message)
            state.viewers().filter { it != userId }.forEach { deliver(it, push) }
        }
        out.toSender?.let { deliver(userId, envelopes.push(Channel.MATCH, MessageType.MATCH_RELAY, JsonObject.serializer(), it)) }
        // A stroke changes the game too: the next host's copy keeps the drawing.
        onChanged()
    }

    suspend fun setAutoplay(userId: String, enabled: Boolean) = act {
        val seat = ownSeat(userId)
        state.update(seat.seat) {
            if (!enabled && presence != SeatPresence.PLAYING) copy(autoplay = false, presence = SeatPresence.PLAYING) else copy(autoplay = enabled)
        }
        if (enabled && state.session.currentSeat == seat.seat) state.botWakeAt = clock.nowMillis()
        publish(emptyList())
    }

    suspend fun setMic(userId: String, on: Boolean) = act {
        state.seatOf(userId)?.let { seat -> state.update(seat.seat) { copy(micOn = on) } }
        publish(emptyList())
    }

    suspend fun setAway(userId: String, away: Boolean) = act {
        val seat = ownSeat(userId)
        state.update(seat.seat) { copy(presence = if (away) SeatPresence.AWAY else SeatPresence.PLAYING) }
        publish(emptyList())
    }

    /** The player gives up their seat: the bot plays it to the end, and they rank last. */
    suspend fun leave(userId: String) = act {
        val seat = ownSeat(userId)
        val out = state.session.stillIn.let { it.isNotEmpty() && seat.seat !in it }
        state.update(seat.seat) { copy(left = true, leftAt = clock.nowMillis(), presence = SeatPresence.BOT, outBeforeLeaving = out) }
        publish(emptyList())
        endIfNoHumans()
    }

    /** Their phone dropped off: after the grace the bot plays their seat until they are back. */
    suspend fun disconnected(userId: String) = act {
        val seat = state.seatOf(userId)
        if (seat != null) {
            state.update(seat.seat) { copy(connected = false, disconnectedAt = clock.nowMillis()) }
        } else {
            state.spectators.remove(userId)
        }
        publish(emptyList())
    }

    suspend fun voteExtension(userId: String, keepPlaying: Boolean) = act {
        val vote = state.extensionVote ?: throw HostRefusal(ErrorCode.ILLEGAL_MOVE, NO_VOTE)
        if (!vote.vote(ownSeat(userId).seat, keepPlaying)) throw HostRefusal(ErrorCode.ILLEGAL_MOVE, CANT_VOTE)
        if (vote.allVoted) decideExtension() else publish(emptyList())
    }

    suspend fun proposeEnd(userId: String) = act {
        val seat = ownSeat(userId)
        val session = state.session
        val now = clock.nowMillis()
        if (seat.presence != SeatPresence.PLAYING) throw HostRefusal(ErrorCode.ILLEGAL_MOVE, CANT_END)
        if (!session.canEndEarly || state.onHold || state.extensionVote != null) throw HostRefusal(ErrorCode.ILLEGAL_MOVE, CANT_END)
        val cooldown = settings[GameSettings.END_VOTE_COOLDOWN_MINUTES] * MILLIS_PER_MINUTE
        state.lastEndProposal[seat.seat]?.let { last -> if (now - last < cooldown) throw HostRefusal(ErrorCode.RATE_LIMITED, CANT_END) }
        state.lastEndProposal[seat.seat] = now
        val vote = HostEndVote(seat.seat, voterSeats(session.stillIn) + seat.seat, now, now + settings[GameSettings.END_VOTE_SECONDS] * HostConstants.MILLIS_PER_SECOND)
        state.endVote = vote
        if (vote.ends || vote.allVoted) decideEnd() else publish(emptyList())
    }

    suspend fun voteEnd(userId: String, agree: Boolean) = act {
        val vote = state.endVote ?: throw HostRefusal(ErrorCode.ILLEGAL_MOVE, NO_VOTE)
        if (!vote.vote(ownSeat(userId).seat, agree)) throw HostRefusal(ErrorCode.ILLEGAL_MOVE, CANT_VOTE)
        if (vote.ends || vote.allVoted || vote.cannotPass) decideEnd() else publish(emptyList())
    }

    /** Runs [block] under the lock, then plans the next bot move or timeout. */
    private suspend fun act(block: () -> Unit) = lock.withLock {
        if (state.ended) throw HostRefusal(ErrorCode.MATCH_ENDED, MATCH_OVER)
        block()
        scheduleWake()
    }

    private fun ownSeat(userId: String): HostMatchSeat = state.seatOf(userId) ?: throw HostRefusal(ErrorCode.NOT_MEMBER, NOT_SEATED)

    // ---------------- votes ----------------

    private fun decideEnd() {
        val vote = state.endVote ?: return
        state.endVote = null
        val ended = vote.ends
        val events = if (ended) state.session.endEarly().orEmpty() else emptyList()
        if (!ended) state.deadline = state.deadline?.plus(clock.nowMillis() - vote.startedAt)
        publish(events + EndVoteDecided(ended, vote.yes.size, vote.voters.size))
        scheduleWake()
    }

    private fun extensionTick(now: Long) {
        val session = state.session
        val vote = state.extensionVote
        when {
            vote != null -> if (now >= vote.endsAt) decideExtension()
            session.atLimit -> {
                val voters = voterSeats(session.stillIn)
                val endsAt = now + state.pauseMs + settings[GameSettings.VOTE_SECONDS] * HostConstants.MILLIS_PER_SECOND
                state.extensionVote = HostExtensionVote(voters, endsAt)
                if (voters.isEmpty()) decideExtension() else publish(emptyList())
            }
        }
    }

    private fun decideExtension() {
        val vote = state.extensionVote ?: return
        state.extensionVote = null
        val continued = vote.continues
        val events = (if (continued) state.session.extend() else state.session.endAtLimit()).orEmpty()
        publish(events + ExtensionDecided(continued, vote.yes.size, vote.voters.size))
        scheduleWake()
    }

    /** Who votes: the people still in, never a CPU or a leaver (a lone human with CPUs still decides). */
    private fun voterSeats(stillIn: Set<Int>): Set<Int> {
        val people = state.seats.filter { it.seat in stillIn && !it.cpu && !it.left }
        val present = people.filter { it.presence == SeatPresence.PLAYING }
        return present.ifEmpty { people }.map { it.seat }.toSet()
    }

    // ---------------- clocks ----------------

    private fun endStartHold() {
        state.startHoldUntil = null
        state.waitingFor.clear()
        state.deadline = null
        publish(emptyList())
        scheduleWake()
    }

    private fun tick() {
        if (state.ended) return
        val now = clock.nowMillis()
        state.startHoldUntil?.let { holdUntil -> if (now >= holdUntil) endStartHold() }
        // A phone that dropped off for longer than the grace: the bot plays for them until they are back.
        state.seats.filter { !it.connected && it.presence == SeatPresence.PLAYING && (it.disconnectedAt ?: now) + HostConstants.DISCONNECT_GRACE_MS <= now }
            .forEach { seat ->
                state.update(seat.seat) { copy(presence = SeatPresence.AWAY) }
                publish(emptyList())
            }
        if (state.endVote == null) state.session.tick(now, state.hostSeats(), state.botSeats())?.let { publish(it, outOfTurn = true) }
        if (!state.ended) endIfNoHumans()
        if (!state.ended) extensionTick(now)
        if (!state.ended) state.endVote?.let { if (now >= it.endsAt) decideEnd() }
        scheduleWake()
    }

    /** Every human has left: the match ends at once, ranked as it stands. */
    private fun endIfNoHumans() {
        if (state.seats.any { !it.cpu && !it.left }) return
        val events = state.session.endWithoutHumans() ?: return
        publish(events)
    }

    /** The turn is due: the host plays it (CPU, away, Autoplay, or the owner's timer ran out). */
    private fun playDueTurn() {
        if (state.ended || state.onHold || state.session.waitsForTick) return
        val session = state.session
        val owner = state.seat(session.currentSeat)
        val mode = if (owner.playedByHost) {
            if (owner.autoplay && !owner.cpu && !owner.left && owner.presence == SeatPresence.PLAYING) BotMode.AUTOPLAY else BotMode.FULL
        } else {
            val deadline = state.deadline ?: return
            if (clock.nowMillis() < deadline) return scheduleWake()
            BotMode.TIMEOUT
        }
        val events = session.botPlay(owner.seat, mode) ?: session.botPlay(owner.seat, BotMode.FULL)
        if (events == null) {
            state.botWakeAt = null
            return
        }
        publish(events)
        scheduleWake()
    }

    /** Wakes the host when the current seat must be played: after the bot's pause, or at the deadline. */
    private fun scheduleWake() {
        val token = ++wakeToken
        if (state.ended || state.onHold || state.session.waitsForTick) return
        val owner = state.seat(state.session.currentSeat)
        val now = clock.nowMillis()
        val wait = if (owner.playedByHost) {
            val deadlineLeft = state.deadline?.let { it - now } ?: Long.MAX_VALUE
            val wakeAt = state.botWakeAt ?: (now + state.pauseMs + state.session.thinkMillis).also { state.botWakeAt = it }
            (wakeAt - now).coerceAtMost(deadlineLeft)
        } else {
            state.botWakeAt = null
            (state.deadline ?: now) - now
        }
        scope.launch {
            delay(wait.coerceAtLeast(0))
            lock.withLock { if (token == wakeToken) runCatching { playDueTurn() } }
        }
    }

    /** A seat the host plays thinks a while before each move, always inside its turn's time. */
    private fun planBotMove(now: Long, ownStep: Boolean) {
        val session = state.session
        val owner = state.seats.firstOrNull { it.seat == session.currentSeat }
        if (owner == null || !owner.playedByHost || session.isFinished) {
            state.botWakeAt = null
            return
        }
        val afterAnimation = now + state.pauseMs
        val planned = state.botWakeAt.takeUnless { ownStep }?.let { maxOf(it, afterAnimation) } ?: (afterAnimation + session.thinkMillis)
        val latest = state.deadline?.let { it - HostConstants.BOT_DEADLINE_MARGIN_MS } ?: planned
        state.botWakeAt = minOf(planned, latest).coerceAtLeast(afterAnimation)
    }

    /** Sends every viewer their snapshot; the turn clock restarts only for a new turn (or when the game asks). */
    private fun publish(events: List<GameEvent>, outOfTurn: Boolean = false) {
        val session = state.session
        val newTurn = session.currentSeat != state.turnOwner
        if (newTurn) state.turnOwner = session.currentSeat
        val now = clock.nowMillis()
        if (events.isNotEmpty()) state.pauseMs = session.pauseAfter(events) + if (newTurn) settings[GameSettings.TURN_GAP_MS] else 0
        val deadline = state.deadline
        val fullTurn = now + state.pauseMs + session.turnSeconds * HostConstants.MILLIS_PER_SECOND
        state.deadline = when {
            deadline == null || newTurn -> fullTurn
            events.isEmpty() -> deadline
            outOfTurn -> maxOf(deadline, now + state.pauseMs)
            session.restartsClockOn(events) -> fullTurn
            else -> now + state.pauseMs + maxOf(deadline - now, settings[GameSettings.TURN_FOLLOW_UP_SECONDS] * HostConstants.MILLIS_PER_SECOND)
        }
        planBotMove(now, events.isNotEmpty() && !outOfTurn)
        state.seats.filter { it.micOn && session.silenced(it.seat) }.forEach { quiet -> state.update(quiet.seat) { copy(micOn = false) } }
        state.version++
        val voteWire = events.filterIsInstance<ExtensionDecided>().map {
            envelopes.json.encodeToJsonElement(ExtensionVotedWire.serializer(), ExtensionVotedWire(it.continued, it.yes, it.voters)).jsonObject
        } + events.filterIsInstance<EndVoteDecided>().map {
            envelopes.json.encodeToJsonElement(EndVotedWire.serializer(), EndVotedWire(it.ended, it.yes, it.voters)).jsonObject
        }
        state.viewers().forEach { viewer ->
            val snapshot = snapshots.build(viewer, now, session.wireEventsFor(events, state.seatOf(viewer)?.seat) + voteWire)
            deliver(viewer, envelopes.push(Channel.MATCH, MessageType.MATCH_SNAPSHOT, MatchSnapshot.serializer(), snapshot))
        }
        if (session.isFinished && !state.ended) finish()
        onChanged()
    }

    /** The result: places only (offline play earns no coins or XP); leavers rank last, the latest leaver first. */
    private fun finish() {
        state.ended = true
        ticker.cancel()
        val gameRanking = state.session.ranking
        val ranking = gameRanking.filterNot { state.seat(it).left } +
            gameRanking.filter { state.seat(it).left }.sortedByDescending { state.seat(it).leftAt ?: 0L }
        val ended = MatchEnded(ranking.mapIndexed { index, seat -> RankedSeat(seat, MatchSeatStatus.ACTIVE, index + 1) })
        val envelope = envelopes.push(Channel.MATCH, MessageType.MATCH_ENDED, MatchEnded.serializer(), ended)
        state.viewers().forEach { deliver(it, envelope) }
        onFinished(state.matchId)
    }

    private companion object {
        const val MILLIS_PER_MINUTE = 60_000L
        const val MATCH_OVER = "The match is over."
        const val NOT_SEATED = "You have no seat at this table."
        const val ON_HOLD = "The match is paused."
        const val UNKNOWN_ACTION = "This game has no such move."
        const val NOT_YOUR_TURN = "It isn't your turn."
        const val ILLEGAL_MOVE = "That move isn't allowed."
        const val NO_VOTE = "There is no vote right now."
        const val CANT_VOTE = "You can't vote on this, or you already did."
        const val CANT_END = "The match can't be ended right now."
    }
}
