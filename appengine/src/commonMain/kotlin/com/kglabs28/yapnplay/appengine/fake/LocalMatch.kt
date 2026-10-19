package com.kglabs28.yapnplay.appengine.fake

import com.kglabs28.yapnplay.appengine.games.base.Game
import com.kglabs28.yapnplay.appengine.games.base.identity.SeatNumber
import com.kglabs28.yapnplay.appengine.games.base.match.GameState
import com.kglabs28.yapnplay.appengine.games.base.match.Viewer
import com.kglabs28.yapnplay.appengine.games.base.play.Action
import com.kglabs28.yapnplay.appengine.games.base.play.ApplyResult
import com.kglabs28.yapnplay.appengine.games.base.play.GameEvent
import com.kglabs28.yapnplay.appengine.games.base.play.GameRandom
import com.kglabs28.yapnplay.appengine.games.mono.data.remote.wire.MonoPublicState
import com.kglabs28.yapnplay.appengine.games.mono.data.remote.wire.MonoWireEvent
import com.kglabs28.yapnplay.appengine.games.mono.domain.MonoAction
import com.kglabs28.yapnplay.appengine.games.mono.domain.MonoEvent
import com.kglabs28.yapnplay.appengine.games.mono.domain.MonoState
import com.kglabs28.yapnplay.appengine.games.mono.domain.rules.MonoRules
import com.kglabs28.yapnplay.appengine.games.mono.engine.MonoEngine
import com.kglabs28.yapnplay.appengine.games.mono.identity.MonoConstants
import com.kglabs28.yapnplay.appengine.games.monochaos.data.remote.wire.MonoChaosPublicState
import com.kglabs28.yapnplay.appengine.games.monochaos.data.remote.wire.MonoChaosWireEvent
import com.kglabs28.yapnplay.appengine.games.monochaos.domain.MonoChaosAction
import com.kglabs28.yapnplay.appengine.games.monochaos.domain.MonoChaosEvent
import com.kglabs28.yapnplay.appengine.games.monochaos.domain.MonoChaosState
import com.kglabs28.yapnplay.appengine.games.monochaos.domain.rules.MonoChaosRules
import com.kglabs28.yapnplay.appengine.games.monochaos.engine.MonoChaosEngine
import com.kglabs28.yapnplay.appengine.games.monochaos.identity.MonoChaosConstants
import com.kglabs28.yapnplay.appengine.protocol.Colour
import com.kglabs28.yapnplay.appengine.protocol.MatchSeatStatus
import com.kglabs28.yapnplay.appengine.protocol.MatchSnapshot
import com.kglabs28.yapnplay.appengine.protocol.SeatInfo
import com.kglabs28.yapnplay.appengine.protocol.SeatPresence
import com.kglabs28.yapnplay.appengine.protocol.TurnInfo
import com.kglabs28.yapnplay.appengine.protocol.ViewerInfo
import com.kglabs28.yapnplay.appengine.protocol.ViewerRole
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

/**
 * One locally-run match. It owns the real [Game] engine, applies the
 * player's actions to it, plays the CPU seats with the game's own bot,
 * and renders the result as the [MatchSnapshot] the app expects. The
 * engine is the real one — only the transport around it is fake.
 */
class LocalMatch(
    val matchId: String,
    val gameId: String,
    private val engine: Game,
    private val random: GameRandom,
    seatCount: Int,
    private val firstCpuSeat: Int,
    val title: String,
) {
    /** Bumped on every change, so a reconnecting viewer still gets the table. */
    private var version = 0L
    private val startedAt = System.currentTimeMillis()
    private var state: GameState = engine.start(defaultRules(), seatCount, random)

    /** The first turn starts now, exactly as the server would time it. */
    private var turnDeadline: Long? = System.currentTimeMillis() + TURN_MS
    private val micOn = mutableMapOf<Int, Boolean>()
    private val autoplay = mutableMapOf<Int, Boolean>()
    private val seatPresence = mutableMapOf<Int, SeatPresence>()
    /**
     * True while the player has the app in the background. A real
     * server hands their turn to the bot when they are away; so does
     * this one, so the behaviour matches before the backend exists.
     */
    @Volatile
    var playerAway: Boolean = false
        private set

    /** The seat the local player holds; they always sit first. */
    private val playerSeat = 1

    private fun defaultRules() = when (gameId) {
        MonoConstants.ID -> MonoRules.TRADITIONAL
        else -> MonoChaosRules.TRADITIONAL
    }

    /** The snapshot for the local player (or a spectator when [asSpectator]). */
    fun snapshot(now: Long, events: List<GameEvent> = emptyList()): MatchSnapshot = snapshotJson(now, events)

    private fun snapshotJson(now: Long, events: List<GameEvent>): MatchSnapshot = MatchSnapshot(
        matchId = matchId,
        version = version,
        serverTime = now,
        viewer = ViewerInfo(
            role = if (asSpectator) ViewerRole.SPECTATOR else ViewerRole.PLAYER,
            seat = if (asSpectator) null else playerSeat,
        ),
        seats = seatInfos(),
        publicState = publicState(if (asSpectator) null else playerSeat),
        legalMoves = emptyList(),
        turn = TurnInfo(seat = state.actorSeat(), phase = phaseName(), deadline = turnDeadline),
        gameId = gameId,
        title = title,
        roomId = null,
        startedAt = startedAt,
        spectators = emptyList(),
        seatOffer = null,
        events = events.mapNotNull { it.toWireJson() },
    )

    /** Set true to watch the table without a seat. */
    var asSpectator = false
        private set

    fun watchAsSpectator() {
        asSpectator = true
    }

    private fun publicState(viewerSeat: Int?): JsonObject {
        val current = state
        return when (current) {
            is MonoState -> LocalWireJson.encode(MonoPublicState.serializer(), current.toPublicState(viewerSeat))
            is MonoChaosState -> LocalWireJson.encode(MonoChaosPublicState.serializer(), current.toPublicState(viewerSeat))
            else -> error("the local host only runs the two MONO games")
        }
    }

    private fun seatInfos(): List<SeatInfo> = state.seats.map { seat ->
        val isCpu = seat.value >= firstCpuSeat
        SeatInfo(
            seat = seat.value,
            colour = SEAT_COLOURS[(seat.value - 1) % SEAT_COLOURS.size],
            controllerUserId = if (isCpu) "cpu-${seat.value}" else "local-player",
            status = MatchSeatStatus.ACTIVE,
            connected = true,
            autoPlay = isCpu || autoplay[seat.value] == true,
            consecutiveMisses = 0,
            cpu = isCpu,
            displayName = if (isCpu) "CPU ${seat.value}" else "You",
            level = if (isCpu) CPU_LEVEL else PLAYER_LEVEL,
            micOn = micOn[seat.value] == true,
            presence = when {
                isCpu -> SeatPresence.BOT
                seat.value == playerSeat && playerAway -> SeatPresence.AWAY
                else -> seatPresence[seat.value] ?: SeatPresence.PLAYING
            },
            avatarId = "",
        )
    }

    private fun phaseName(): String {
        val current = state
        return when (current) {
            is MonoState -> current.phase.name
            is MonoChaosState -> current.phase.name
            else -> PHASE_DEFAULT
        }
    }

    /** Applies the player's [action] and returns the events it caused. */
    fun apply(actionJson: JsonObject, now: Long): List<GameEvent> {
        val action = decode(actionJson) ?: return emptyList()
        val result = engine.apply(state, SeatNumber(playerSeat), action, random)
        if (result !is ApplyResult.Success) return emptyList()
        state = result.state
        version++
        turnDeadline = now + TURN_MS
        return result.events
    }

    /**
     * Plays CPU seats with the game's own bot until it is the player's
     * turn again, the match ends, or the guard stops it.
     */
    fun playCpus(now: Long): List<GameEvent> {
        val collected = mutableListOf<GameEvent>()
        var guard = 0
        while (guard++ < CPU_TURN_GUARD) {
            if (engine.isFinished(state)) break
            val seat = state.actorSeat()
            if (seat == playerSeat) break
            val result = engine.autoplay(state, SeatNumber(seat), random)
            if (result !is ApplyResult.Success) break
            state = result.state
            version++
            collected += result.events
        }
        if (collected.isNotEmpty()) turnDeadline = now + TURN_MS
        return collected
    }

    fun setAutoplay(seat: Int, enabled: Boolean) {
        autoplay[seat] = enabled
    }

    /** The player backgrounded the app; their turns play without them. */
    fun setAway(away: Boolean) {
        playerAway = away
        seatPresence[playerSeat] = if (away) SeatPresence.AWAY else SeatPresence.PLAYING
    }

    /**
     * Whether the server would play this seat's turn for it: a CPU, a
     * seat with autoplay on, or the player while they are away.
     */
    private fun serverPlaysFor(seat: Int): Boolean =
        seat >= firstCpuSeat || autoplay[seat] == true || (seat == playerSeat && playerAway)

    /** How long is left on [seat]'s turn, in millis; null when untimed. */
    fun remainingTurnMs(now: Long): Long? {
        val deadline = turnDeadline ?: return null
        return (deadline - now).coerceAtLeast(0)
    }

    /**
     * The seat whose turn the server should play, or null. The rules a
     * real backend uses: a CPU always plays at once; a seat with
     * autoplay on, or the player while they are away, plays at once;
     * anyone else plays only once their turn timer has run out.
     */
    fun seatToAutoplay(now: Long): Int? {
        if (engine.isFinished(state)) return null
        val seat = state.actorSeat()
        val playsAtOnce = seat >= firstCpuSeat ||
            autoplay[seat] == true ||
            (seat == playerSeat && playerAway)
        // A live human waits for the clock, as they would on the server.
        val timedOut = remainingTurnMs(now) == 0L
        return seat.takeIf { playsAtOnce || timedOut }
    }

    /** The engine plays [seat]'s turn, as the server would on a timeout. */
    fun autoplaySeat(seat: Int, now: Long): List<GameEvent> {
        val result = engine.autoplay(state, SeatNumber(seat), random)
        if (result !is ApplyResult.Success) return emptyList()
        state = result.state
        version++
        turnDeadline = now + TURN_MS
        return result.events
    }

    fun setMic(seat: Int, on: Boolean) {
        micOn[seat] = on
    }

    fun isFinished(): Boolean = engine.isFinished(state)

    /** The wire action this game understands; null when it is not one of ours. */
    private fun decode(json: JsonObject): Action? = when (gameId) {
        MonoConstants.ID -> when (json.wireType()) {
            "PlayCard" -> MonoAction.PlayCard(json.int(HAND_INDEX))
            "DrawCard" -> MonoAction.DrawCard
            "ChooseColor" -> MonoAction.ChooseColor(json.monoColour() ?: MonoConstants.Color.RED)
            "CallMono" -> MonoAction.CallMono
            "AcceptStack" -> MonoAction.AcceptStack
            else -> null
        }
        else -> when (json.wireType()) {
            "PlayCard" -> MonoChaosAction.PlayCard(json.int(HAND_INDEX))
            "DrawUntilPlayable" -> MonoChaosAction.DrawUntilPlayable
            "ChooseColor" -> MonoChaosAction.ChooseColor(json.chaosColour() ?: MonoChaosConstants.Color.RED)
            "ChooseRouletteColor" -> MonoChaosAction.ChooseRouletteColor(json.chaosColour() ?: MonoChaosConstants.Color.RED)
            "CallMono" -> MonoChaosAction.CallMono
            "StackDraw" -> MonoChaosAction.StackDraw(json.int(HAND_INDEX))
            "AcceptStack" -> MonoChaosAction.AcceptStack
            else -> null
        }
    }

    /** The colour named on the wire; null when it is not one we know. */
    private fun JsonObject.monoColour(): MonoConstants.Color? =
        text(COLOR)?.let { name -> MonoConstants.Color.entries.firstOrNull { it.name == name } }

    /** The colour named on the wire; null when it is not one we know. */
    private fun JsonObject.chaosColour(): MonoChaosConstants.Color? =
        text(COLOR)?.let { name -> MonoChaosConstants.Color.entries.firstOrNull { it.name == name } }

    private companion object {
        const val TURN_MS = 15_000L
        const val CPU_TURN_GUARD = 64
        const val CPU_LEVEL = 5
        const val PLAYER_LEVEL = 12
        const val PHASE_DEFAULT = "AWAITING_PLAY"
        const val HAND_INDEX = "handIndex"
        const val COLOR = "color"

        val SEAT_COLOURS = listOf(
            Colour.RED,
            Colour.GREEN,
            Colour.BLUE,
            Colour.YELLOW,
            Colour.PURPLE,
            Colour.ORANGE,
        )
    }
}

/** The "type" of a wire action. */
private fun JsonObject.wireType(): String? = (this[TYPE] as? JsonPrimitive)?.content

private fun JsonObject.int(key: String): Int = (this[key] as? JsonPrimitive)?.content?.toIntOrNull() ?: 0

private fun JsonObject.text(key: String): String? = (this[key] as? JsonPrimitive)?.content

private const val TYPE = "type"

/** A game event as the wire JSON the app's mapper decodes. */
internal fun GameEvent.toWireJson(): JsonObject? = when (this) {
    is MonoEvent -> LocalWireJson.encodeOrNull(MonoWireEvent.serializer(), toWire())
    is MonoChaosEvent -> LocalWireJson.encodeOrNull(MonoChaosWireEvent.serializer(), toWire())
    else -> null
}

/** Whether the local host can run [gameId] (the two MONO games). */
fun isLocallyHosted(gameId: String): Boolean = gameId == MonoConstants.ID || gameId == MonoChaosConstants.ID

/** The engine the local host runs for [gameId]. */
fun localEngineFor(gameId: String): Game = when (gameId) {
    MonoConstants.ID -> MonoEngine()
    MonoChaosConstants.ID -> MonoChaosEngine()
    else -> error("the local host only runs the two MONO games, not $gameId")
}

/** The viewer's seat for [seat], used when the app asks for a player's view. */
internal fun localViewer(seat: Int): Viewer = Viewer.Player(SeatNumber(seat))