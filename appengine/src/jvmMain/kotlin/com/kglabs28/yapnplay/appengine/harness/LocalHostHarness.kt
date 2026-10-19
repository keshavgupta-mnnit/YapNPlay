package com.kglabs28.yapnplay.appengine.harness

import com.kglabs28.yapnplay.appengine.fake.FakeRealtimeConnection
import com.kglabs28.yapnplay.appengine.games.base.play.DefaultGameRandom
import com.kglabs28.yapnplay.appengine.games.mono.data.remote.MonoSnapshotMapper
import com.kglabs28.yapnplay.appengine.games.mono.identity.MonoConstants
import com.kglabs28.yapnplay.appengine.games.monochaos.data.remote.MonoChaosSnapshotMapper
import com.kglabs28.yapnplay.appengine.games.monochaos.identity.MonoChaosConstants
import com.kglabs28.yapnplay.appengine.protocol.ConfirmPresence
import com.kglabs28.yapnplay.appengine.protocol.GameAction
import com.kglabs28.yapnplay.appengine.protocol.JoinQueue
import com.kglabs28.yapnplay.appengine.protocol.MatchRequestSnapshot
import com.kglabs28.yapnplay.appengine.protocol.MatchFound
import com.kglabs28.yapnplay.appengine.protocol.MatchSnapshot
import com.kglabs28.yapnplay.appengine.protocol.SetPresence
import com.kglabs28.yapnplay.appengine.protocol.transport.Channel
import com.kglabs28.yapnplay.appengine.protocol.transport.MessageType
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

/**
 * Drives the local host the way the app does — Quick Play, then a table
 * asking for its snapshot, then playing cards — and prints what each
 * table looked like. A console check, not a unit test.
 */
object LocalHostHarness {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
        val realtime = FakeRealtimeConnection(json, scope, DefaultGameRandom())
        realtime.startTicker()
        realtime.connect()
        println("connect -> status=${realtime.status.value} connections=${realtime.connections.value}")

        playOne(scope, realtime, MonoConstants.ID)
        playOne(scope, realtime, MonoChaosConstants.ID)
        serverBehaviours(scope, realtime)
        println("\nDONE")
    }

    /**
     * The two things the backend does that a tap cannot: hand the
     * player's turn over while the app is in the background, and play a
     * seat whose turn timer ran out. Both are observed by polling the
     * table, the way the app's own snapshot stream sees them.
     */
    private suspend fun serverBehaviours(scope: CoroutineScope, realtime: FakeRealtimeConnection) {
        println("\n=========== server behaviours ===========")
        val gameId = MonoConstants.ID
        val matchId = joinQueue(scope, realtime, gameId) ?: run {
            println("FAIL: no match found")
            return
        }
        val first = openTable(scope, realtime, matchId) ?: run {
            println("FAIL: no snapshot")
            return
        }
        val before = seatOf(gameId, first)

        // 1. The player goes away: the server must play their turn.
        realtime.request(
            Channel.MATCH,
            MessageType.SET_PRESENCE,
            SetPresence.serializer(),
            SetPresence(matchId, true),
        )
        val moved = awaitSeatChange(scope, realtime, gameId, matchId, before, AWAY_WAIT_MS)
        println(if (moved != null) {
            "OK    away handover: seat $before -> $moved played with no tap"
        } else {
            "FAIL  away handover: still seat $before after going away"
        })

        // 2. Back in the app, then let the next turn time out on its own.
        realtime.request(
            Channel.MATCH,
            MessageType.CONFIRM_PRESENCE,
            ConfirmPresence.serializer(),
            ConfirmPresence(matchId),
        )
        val live = moved ?: before
        val timedOut = awaitSeatChange(scope, realtime, gameId, matchId, live, TIMEOUT_WAIT_MS)
        println(if (timedOut != null) {
            "OK    turn timeout: seat $live timed out, the table moved to $timedOut"
        } else {
            "FAIL  turn timeout: seat $live never timed out"
        })
    }

    /**
     * Asks the server for the table and returns the snapshot it answers
     * with. The watcher is subscribed *before* the request, because the
     * stream carries no replay — asking first would miss the answer.
     */
    private suspend fun openTable(
        scope: CoroutineScope,
        realtime: FakeRealtimeConnection,
        matchId: String,
    ): MatchSnapshot? {
        val got = CompletableDeferred<MatchSnapshot>()
        val watch = scope.launch {
            realtime.stream(Channel.MATCH)
                .map { realtime.decode(it, MatchSnapshot.serializer()) }
                .collect { snapshot ->
                    if (snapshot.matchId == matchId && !got.isCompleted) got.complete(snapshot)
                }
        }
        delay(SUBSCRIBE_MS)
        realtime.request(
            Channel.MATCH,
            MessageType.MATCH_REQUEST_SNAPSHOT,
            MatchRequestSnapshot.serializer(),
            MatchRequestSnapshot(matchId, 0),
        )
        val snapshot = withTimeoutOrNull(OPEN_WAIT_MS) { got.await() }
        watch.cancel()
        return snapshot
    }

    /**
     * Polls the table until the turn leaves [from], or [limitMs] runs
     * out. Polling rather than listening, so a turn that lands between
     * requests is still seen — the same table the app is looking at.
     */
    private suspend fun awaitSeatChange(
        scope: CoroutineScope,
        realtime: FakeRealtimeConnection,
        gameId: String,
        matchId: String,
        from: Int,
        limitMs: Long,
    ): Int? {
        val deadline = System.currentTimeMillis() + limitMs
        while (System.currentTimeMillis() < deadline) {
            delay(POLL_MS)
            val snapshot = openTable(scope, realtime, matchId) ?: continue
            val seat = seatOf(gameId, snapshot)
            if (seat != from) return seat
        }
        return null
    }

    /** Whose turn the snapshot says it is. */
    private fun seatOf(gameId: String, snapshot: MatchSnapshot): Int =
        if (gameId == MonoConstants.ID) {
            MonoSnapshotMapper(json).map(snapshot, "local-player", Clock.System.now()).match.currentSeat
        } else {
            MonoChaosSnapshotMapper(json).map(snapshot, "local-player", Clock.System.now()).match.currentSeat
        }

    private suspend fun playOne(scope: CoroutineScope, realtime: FakeRealtimeConnection, gameId: String) {
        println("\n=========== $gameId ===========")
        val matchId = joinQueue(scope, realtime, gameId) ?: run {
            println("FAIL: no match found for $gameId")
            return
        }
        realtime.request(
            Channel.MATCH,
            MessageType.MATCH_REQUEST_SNAPSHOT,
            MatchRequestSnapshot.serializer(),
            MatchRequestSnapshot(matchId, 0),
        )
        val first = awaitSnapshot(realtime, matchId) ?: run {
            println("FAIL: no snapshot for $gameId")
            return
        }
        report(gameId, first)

        // Play up to eight turns the way a player would: pick a card the
        // table actually allows, send it, and let the CPUs answer.
        var current = first
        repeat(PLAYS) { turn ->
            val hand = myHand(gameId, current)
            if (hand.isEmpty()) {
                println("  hand empty, stopping")
                return@repeat
            }
            val topColour = topColourOf(current)
            val topNumber = topNumberOf(current)
            // Only legal cards, so the table never rejects the move.
            val index = hand.indexOfFirst { it.matches(topColour, topNumber) }
            if (index < 0) {
                println("  turn ${turn + 1}: nothing playable in hand (top=$topColour/$topNumber), drawing instead")
                sendAction(realtime, matchId, buildJsonObject { put("type", drawType(gameId)) })
            } else {
                sendAction(realtime, matchId, playCard(index))
            }
            var next = awaitSnapshot(realtime, matchId, skip = current.version)
            if (next == null) {
                println("  turn ${turn + 1}: no snapshot after the move")
                return@repeat
            }
            // A Prism leaves the colour open: the app shows the picker
            // and sends the choice, so the harness does the same.
            if (phaseOf(next) == PHASE_COLOR) {
                sendAction(
                    realtime,
                    matchId,
                    buildJsonObject {
                        put("type", chooseColourType(gameId))
                        put("color", COLOUR_NAME)
                    },
                )
                next = awaitSnapshot(realtime, matchId, skip = next.version) ?: next
            }
            current = next
            report(gameId, next, prefix = "  turn ${turn + 1} ->")
        }
    }

    /** Standard MONO draws one card; Complete Chaos draws until playable. */
    private fun drawType(gameId: String) =
        if (gameId == MonoConstants.ID) "DrawCard" else "DrawUntilPlayable"

    private fun phaseOf(snapshot: MatchSnapshot): String? =
        (snapshot.publicState["phase"] as? JsonPrimitive)?.content

    private fun chooseColourType(gameId: String) = "ChooseColor"

    private fun topColourOf(snapshot: MatchSnapshot): String? =
        (snapshot.publicState["topCardColor"] as? JsonPrimitive)?.content

    private fun topNumberOf(snapshot: MatchSnapshot): String? {
        val top = snapshot.publicState["topCard"] as? JsonObject ?: return null
        return (top["number"] as? JsonPrimitive)?.content
    }

    /** A card the table would accept: same colour, same number, or a Prism. */
    private fun String.matches(colour: String?, number: String?): Boolean {
        if (this.contains("Prism")) return true
        if (colour != null && this.contains("color=$colour")) return true
        if (number != null && this.contains("number=$number")) return true
        return false
    }

    /** Joins the queue and returns the match id the server handed out. */
    private suspend fun joinQueue(
        scope: CoroutineScope,
        realtime: FakeRealtimeConnection,
        gameId: String,
    ): String? {
        val found = CompletableDeferred<String>()
        // Listened to until the id arrives, so the listener is not torn
        // down before the server has registered the match.
        val listener = scope.launch {
            realtime.stream(Channel.MATCHMAKING)
                .filter { it.type == MessageType.MATCH_FOUND }
                .map { realtime.decode(it, MatchFound.serializer()) }
                .collect { if (!found.isCompleted) found.complete(it.matchId) }
        }
        realtime.request(Channel.MATCHMAKING, MessageType.JOIN_QUEUE, JoinQueue.serializer(), JoinQueue(gameId))
        val id = withTimeoutOrNull(QUEUE_WAIT_MS) { found.await() }
        listener.cancel()
        // Give the server a beat to finish registering the table, as the
        // app's own queue flow does before it opens the match screen.
        delay(AFTER_JOIN_MS)
        return id
    }

    private suspend fun sendAction(realtime: FakeRealtimeConnection, matchId: String, action: JsonObject) {
        realtime.request(
            Channel.MATCH,
            MessageType.GAME_ACTION,
            GameAction.serializer(),
            GameAction(matchId, action),
        )
        // The host waits a beat before letting the CPUs answer.
        delay(500)
    }

    private fun playCard(index: Int): JsonObject = buildJsonObject {
        put("type", "PlayCard")
        put("handIndex", index)
    }

    private suspend fun awaitSnapshot(
        realtime: FakeRealtimeConnection,
        matchId: String,
        skip: Long = -1,
    ): MatchSnapshot? = withTimeoutOrNull(3_000) {
        realtime.stream(Channel.MATCH)
            .map { realtime.decode(it, MatchSnapshot.serializer()) }
            .filter { it.matchId == matchId && it.version > skip }
            .first()
    }

    private fun report(gameId: String, snapshot: MatchSnapshot, prefix: String = "  ") {
        if (gameId == MonoConstants.ID) {
            val m = MonoSnapshotMapper(json).map(snapshot, "local-player", Clock.System.now()).match
            println(
                "$prefix v${snapshot.version} phase=${m.phase} seat=${m.currentSeat} " +
                    "top=${m.topCard?.let { it.short() }} hand=${m.me?.hand?.size} " +
                    "counts=${m.players.map { it.cardCount }}"
            )
        } else {
            val m = MonoChaosSnapshotMapper(json).map(snapshot, "local-player", Clock.System.now()).match
            println(
                "$prefix v${snapshot.version} phase=${m.phase} seat=${m.currentSeat} " +
                    "top=${m.topCard?.let { it.short() }} hand=${m.me?.hand?.size} " +
                    "counts=${m.players.map { it.cardCount }} stack=${m.activeStackPenalty}"
            )
        }
    }

    private fun myHand(gameId: String, snapshot: MatchSnapshot): List<String> {
        val element = snapshot.publicState["hands"] ?: return emptyList()
        return runCatching {
            when (gameId) {
                MonoConstants.ID -> MonoSnapshotMapper(json).map(snapshot, "local-player", Clock.System.now())
                    .match.me?.hand.orEmpty().map { it.short() }
                else -> MonoChaosSnapshotMapper(json).map(snapshot, "local-player", Clock.System.now())
                    .match.me?.hand.orEmpty().map { it.short() }
            }
        }.getOrDefault(emptyList())
    }

    /** "NumberCard(color=RED, number=7)" -> "NumberCard(RED,7)". */
    private fun Any.short(): String = toString().substringBefore('@').trim()

    private const val PLAYS = 8
    private const val PHASE_COLOR = "AWAITING_COLOR_CHOICE"
    private const val COLOUR_NAME = "GREEN"
    private const val AWAY_WAIT_MS = 8_000L
    private const val TIMEOUT_WAIT_MS = 25_000L
    private const val QUEUE_WAIT_MS = 5_000L
    private const val AFTER_JOIN_MS = 300L
    private const val SUBSCRIBE_MS = 200L
    private const val POLL_MS = 400L
    private const val OPEN_WAIT_MS = 3_000L
}