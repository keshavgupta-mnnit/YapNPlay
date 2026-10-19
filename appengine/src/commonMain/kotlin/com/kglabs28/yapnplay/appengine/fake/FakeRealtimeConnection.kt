package com.kglabs28.yapnplay.appengine.fake

import com.kglabs28.yapnplay.appengine.core.common.CrashReporter
import com.kglabs28.yapnplay.appengine.core.network.ConnectionStatus
import com.kglabs28.yapnplay.appengine.core.network.RealtimeGateway
import com.kglabs28.yapnplay.appengine.core.network.RealtimeRejectedException
import com.kglabs28.yapnplay.appengine.games.base.play.DefaultGameRandom
import com.kglabs28.yapnplay.appengine.games.base.play.GameEvent
import com.kglabs28.yapnplay.appengine.games.base.play.GameRandom
import com.kglabs28.yapnplay.appengine.protocol.ErrorCode
import com.kglabs28.yapnplay.appengine.protocol.GameAction
import com.kglabs28.yapnplay.appengine.protocol.JoinQueue
import com.kglabs28.yapnplay.appengine.protocol.MatchFound
import com.kglabs28.yapnplay.appengine.protocol.MatchRequestSnapshot
import com.kglabs28.yapnplay.appengine.protocol.ConfirmPresence
import com.kglabs28.yapnplay.appengine.protocol.MatchSnapshot
import com.kglabs28.yapnplay.appengine.protocol.QueueCancelReason
import com.kglabs28.yapnplay.appengine.protocol.QueueCancelled
import com.kglabs28.yapnplay.appengine.protocol.QueueStatus
import com.kglabs28.yapnplay.appengine.protocol.SetAutoplay
import com.kglabs28.yapnplay.appengine.protocol.SetMic
import com.kglabs28.yapnplay.appengine.protocol.SetPresence
import com.kglabs28.yapnplay.appengine.protocol.transport.Channel
import com.kglabs28.yapnplay.appengine.protocol.transport.Envelope
import com.kglabs28.yapnplay.appengine.protocol.transport.MessageType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

/**
 * A stand-in for the backend WebSocket, used while the real server has
 * no MONO games yet. It is always "connected", answers the requests the
 * app makes, and pushes snapshots from a locally-run [LocalMatch] — the
 * real engine, a fake transport. Games it does not host are refused with
 * [ErrorCode.GAME_UNAVAILABLE], so the app's own error paths still run.
 */
class FakeRealtimeConnection(
    private val json: Json,
    private val scope: CoroutineScope,
    private val random: GameRandom = DefaultGameRandom(),
) : RealtimeGateway {
    private val incoming = MutableSharedFlow<Envelope>(extraBufferCapacity = INCOMING_BUFFER)
    private val matches = mutableMapOf<String, LocalMatch>()
    private var matchCounter = 0

    /**
     * The tables are touched by the client's requests and by the server
     * beats at the same time, so every mutation is under one lock —
     * exactly as a real backend serialises actions per match.
     */
    private val tableLock = Mutex()
    // Starts at 0 and goes to 1 on connect(), exactly like the real
    // socket: MatchChannel asks for a snapshot on that transition, and
    // a table that never sees it would sit on "Connecting" forever.
    private val _connections = MutableStateFlow(0)
    private val _connected = MutableStateFlow(false)
    private val _status = MutableStateFlow(ConnectionStatus.CONNECTING)

    override val connections: StateFlow<Int> = _connections.asStateFlow()
    override val connected: StateFlow<Boolean> = _connected.asStateFlow()
    override val status: StateFlow<ConnectionStatus> = _status.asStateFlow()

    /** Nothing to dial; the fake is online the moment the app asks. */
    override fun connect() {
        _connections.value = _connections.value + 1
        _connected.value = true
        _status.value = ConnectionStatus.ONLINE
    }

    /** Same: there is nothing to retry. */
    override fun retryNow() = connect()

    override fun stream(channel: Channel): Flow<Envelope> = incoming.filter { it.channel == channel }

    override fun <T> decode(envelope: Envelope, serializer: KSerializer<T>): T =
        json.decodeFromJsonElement(serializer, envelope.payload)

    /**
     * Answers a client request the way the server would. Requests that
     * need the backend (rooms, moderation) are acknowledged without
     * doing anything, so no screen blocks on them.
     */
    override suspend fun <T> request(
        channel: Channel,
        type: String,
        serializer: KSerializer<T>,
        value: T,
    ): Envelope {
        when (channel to type) {
            // Quick Play: find a match straight away for the two MONO games.
            Channel.MATCHMAKING to MessageType.JOIN_QUEUE -> {
                val gameId = (value as? JoinQueue)?.gameId.orEmpty()
                startQueue(gameId)
                return ack(channel)
            }
            Channel.MATCHMAKING to MessageType.LEAVE_QUEUE -> return ack(channel)

            // MATCH: the snapshot a table asks for when it opens.
            Channel.MATCH to MessageType.MATCH_REQUEST_SNAPSHOT -> {
                val matchId = (value as? MatchRequestSnapshot)?.matchId.orEmpty()
                pushSnapshot(matchId)
                return ack(channel)
            }
            // MATCH: a game action (play a card, draw, pick a colour…).
            Channel.MATCH to MessageType.GAME_ACTION -> {
                val action = value as? GameAction
                if (action != null) runAction(action)
                return ack(channel)
            }
            Channel.MATCH to MessageType.SET_AUTOPLAY -> {
                (value as? SetAutoplay)?.let { matches[it.matchId]?.setAutoplay(PLAYER_SEAT, it.enabled) }
                return ack(channel)
            }
            Channel.MATCH to MessageType.SET_MIC -> {
                (value as? SetMic)?.let { matches[it.matchId]?.setMic(PLAYER_SEAT, it.micOn) }
                return ack(channel)
            }
            // The player backgrounded the app: the server takes their turn.
            Channel.MATCH to MessageType.SET_PRESENCE -> {
                (value as? SetPresence)?.let { matches[it.matchId]?.setAway(it.away) }
                return ack(channel)
            }
            Channel.MATCH to MessageType.CONFIRM_PRESENCE -> {
                (value as? ConfirmPresence)?.let { matches[it.matchId]?.setAway(false) }
                return ack(channel)
            }
            else -> return ack(channel)
        }
    }

    /** Opens a local match for [gameId] and tells the app it was found. */
    private suspend fun startQueue(gameId: String) {
        val now = System.currentTimeMillis()
        if (!isLocallyHosted(gameId)) {
            push(
                Channel.MATCHMAKING,
                MessageType.QUEUE_CANCELLED,
                LocalWireJson.encode(
                    QueueCancelled.serializer(),
                    QueueCancelled(gameId, QueueCancelReason.GAME_UNAVAILABLE),
                ),
            )
            return
        }
        // Every Quick Play search opens a fresh match, as a real server does.
        val matchId = "local-$gameId-${nextMatchNumber()}"
        val match = LocalMatch(
            matchId = matchId,
            gameId = gameId,
            engine = localEngineFor(gameId),
            random = random,
            seatCount = LOCAL_SEAT_COUNT,
            firstCpuSeat = PLAYER_SEAT + 1,
            title = LOCAL_TITLE,
        )
        // Opening the match runs the CPUs up to the player's first turn,
        // so the table is already a real game when it first appears.
        match.playCpus(now)
        matches[matchId] = match
        // A short search so the Quick Play animation still plays.
        push(
            Channel.MATCHMAKING,
            MessageType.QUEUE_STATUS,
            LocalWireJson.encode(
                QueueStatus.serializer(),
                QueueStatus(
                    gameId = gameId,
                    playersFound = 1,
                    playersNeeded = LOCAL_SEAT_COUNT,
                    searchDeadline = now + SEARCH_MS,
                    serverTime = now,
                ),
            ),
        )
        push(
            Channel.MATCHMAKING,
            MessageType.MATCH_FOUND,
            LocalWireJson.encode(
                MatchFound.serializer(),
                MatchFound(
                    matchId = matchId,
                    gameId = gameId,
                    humanPlayers = 1,
                    cpuPlayers = LOCAL_SEAT_COUNT - 1,
                ),
            ),
        )
        // The table only receives snapshots it asked for, so send the
        // first one now: whoever opens the match screen then has it.
        pushSnapshot(matchId)
    }

    /** A fresh id per search, the way the backend hands out match ids. */
    private fun nextMatchNumber(): Int = matchCounter++

    /** Applies the player's action, lets the CPUs answer, pushes the result. */
    private suspend fun runAction(action: GameAction) {
        val now = System.currentTimeMillis()
        // The whole turn resolves under the lock, so a server beat
        // cannot act on the table halfway through a move.
        val events = tableLock.withLock {
            val match = matches[action.matchId]
                ?: throw RealtimeRejectedException(ErrorCode.NOT_FOUND, "no such match")
            val played = match.apply(action.action, now)
            if (played.isEmpty()) return@withLock emptyList()
            played + match.playCpus(System.currentTimeMillis())
        }
        if (events.isEmpty()) return
        // Give the table a beat so the player sees their own card land.
        delay(AFTER_ACTION_MS)
        pushSnapshot(action.matchId, events)
    }

    /** Sends the match's current table to whoever is watching it. */
    private suspend fun pushSnapshot(matchId: String, events: List<GameEvent> = emptyList()) {
        val now = System.currentTimeMillis()
        val encoded = tableLock.withLock {
            val match = matches[matchId]
                ?: throw RealtimeRejectedException(ErrorCode.NOT_FOUND, "no such match")
            LocalWireJson.encode(MatchSnapshot.serializer(), match.snapshot(now, events))
        }
        push(Channel.MATCH, MessageType.MATCH_SNAPSHOT, encoded)
    }

    private fun ack(channel: Channel): Envelope = Envelope(
        requestId = LOCAL_REQUEST_ID,
        channel = channel,
        type = MessageType.ACK,
        payload = JsonObject(emptyMap()),
    )

    /** Sends an envelope to the app. */
    private suspend fun push(channel: Channel, type: String, payload: JsonObject) {
        incoming.emit(Envelope(LOCAL_PUSH_ID, channel, type, payload))
    }

    /** Kicks off the local matches' server beats; call once at startup. */
    fun startTicker() {
        scope.launch {
            while (isActive) {
                delay(TICK_MS)
                // A snapshot of the live tables: the map changes as
                // matches open, so it is read each beat.
                val live = matches.values.filter { !it.isFinished() }
                for (match in live) {
                    runCatching { beat(match) }
                        .onFailure { CrashReporter.record(it, "FakeRealtimeConnection.ticker") }
                }
            }
        }
    }

    /**
     * One server beat for [match], exactly as the backend would act:
     * play any seat whose turn the server holds (a CPU, an autoplay or
     * away seat, or a live seat whose timer ran out), then push the
     * table. This is what makes the local mode behave like a real game
     * rather than only responding to taps.
     */
    private suspend fun beat(match: LocalMatch) {
        val events = mutableListOf<GameEvent>()
        // Bounded so a stuck table cannot spin here forever. The lock is
        // taken per turn, never across the pause, so a client's action
        // is never stuck behind a whole chain of CPU turns.
        var guard = 0
        while (guard++ < BEAT_TURN_LIMIT) {
            val now = System.currentTimeMillis()
            val moved = tableLock.withLock {
                val seat = match.seatToAutoplay(now) ?: return@withLock null
                match.autoplaySeat(seat, now)
            } ?: break
            if (moved.isEmpty()) break
            events += moved
            // Let the table settle so the events are not all in one push.
            delay(BEAT_GAP_MS)
        }
        // A live human's turn pushes a fresh snapshot so their timer and
        // the card borders keep moving, exactly as the server would.
        pushSnapshot(match.matchId, events)
    }

    private companion object {
        const val INCOMING_BUFFER = 256
        const val LOCAL_REQUEST_ID = "local-request"
        const val LOCAL_PUSH_ID = "local-push"
        const val PLAYER_SEAT = 1
        const val LOCAL_SEAT_COUNT = 4
        const val LOCAL_TITLE = "Local Practice"
        const val SEARCH_MS = 400L
        const val AFTER_ACTION_MS = 250L
        const val TICK_MS = 500L
        const val BEAT_GAP_MS = 300L
        const val BEAT_TURN_LIMIT = 32
    }
}