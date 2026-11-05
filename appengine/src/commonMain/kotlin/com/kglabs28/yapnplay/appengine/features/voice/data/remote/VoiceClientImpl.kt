package com.kglabs28.yapnplay.appengine.features.voice.data.remote

import com.kglabs28.yapnplay.appengine.protocol.VoiceToken
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.Clock
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import com.kglabs28.yapnplay.appengine.features.voice.domain.MySpeech
import com.kglabs28.yapnplay.appengine.core.audio.MicLevelMonitor
import com.kglabs28.yapnplay.appengine.core.common.CrashReporter
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * Live voice. Each screen that talks (lobby, match table) collects
 * [speakers] for its target; the newest one still open decides which
 * voice room the device is in, so closing a match drops back to the
 * lobby's room. A match has its own voice room (its players and
 * spectators), apart from its lobby's, so the lobby never hears the table.
 *
 * The voice room is joined only while the screen wants it
 * ([setVoiceWanted]): another person is there and someone's mic is on.
 * Home, other screens and bot-only tables use no voice session. The mic
 * is published only while the screen opens it ([setMicOpen]).
 * Muting someone stops *this* device playing their audio — they stay
 * audible to everyone else. Without a voice room (backend has no voice
 * configured, or it can't be reached) the mic level is still read so
 * the user's own talking wave works. With the mic off (app in front) the
 * level is read too, only to tell the user they're talking on mute.
 * If someone there has their mic on but this device keeps not receiving
 * them (its voice session missed them coming back), it rejoins the voice
 * room by itself, as leaving and coming back would.
 */
class VoiceClientImpl(
    private val mic: MicLevelMonitor,
    private val engine: VoiceEngine,
    private val tokens: VoiceTokenApi,
    private val json: Json,
    scope: CoroutineScope,
) : VoiceClient {

    private val muted = MutableStateFlow<Set<String>>(emptySet())
    override val locallyMuted: StateFlow<Set<String>> = muted.asStateFlow()

    override fun setLocallyMuted(userId: String, muted: Boolean) {
        this.muted.update { if (muted) it + userId else it - userId }
        engine.setMuted(this.muted.value)
    }

    private val speaker = MutableStateFlow(true)
    override val speakerOn: StateFlow<Boolean> = speaker.asStateFlow()

    override fun setSpeakerOn(on: Boolean) {
        speaker.value = on
        engine.setSpeakerOn(on)
    }

    private val micSwitch = MutableStateFlow(false)
    override val micOn: StateFlow<Boolean> = micSwitch.asStateFlow()

    override fun setMicOn(on: Boolean) {
        micSwitch.value = on
    }

    private val micOpen = MutableStateFlow(false)
    private val listenMuted = MutableStateFlow(false)

    override val mySpeech: StateFlow<MySpeech> =
        combine(engine.connected, engine.localSpeaking, mic.speaking, micOpen) { connected, voice, level, open ->
            when {
                open && (if (connected) voice else level) -> MySpeech.SPEAKING
                !open && level -> MySpeech.SPEAKING_MUTED
                else -> MySpeech.SILENT
            }
        }.stateIn(scope, SharingStarted.Eagerly, MySpeech.SILENT)

    override fun setMicOpen(open: Boolean, listenMuted: Boolean) {
        micOpen.value = open
        this.listenMuted.value = listenMuted
    }

    /** Screens currently wanting voice, oldest first. */
    private val open = MutableStateFlow<List<Entry>>(emptyList())

    private class Entry(val target: VoiceTarget)

    override val activeTarget: Flow<VoiceTarget?> get() = open.map { it.lastOrNull()?.target }.distinctUntilChanged()

    /** Targets whose screen needs voice now (someone to talk with, a mic on). Declared before init, which reads it. */
    private val wanted = MutableStateFlow<Set<VoiceTarget>>(emptySet())
    /** Per target, the others there with their mic on (who should be heard). */
    private val talkers = MutableStateFlow<Map<VoiceTarget, Set<String>>>(emptyMap())

    override fun speakers(target: VoiceTarget): Flow<Set<String>> = flow {
        val entry = Entry(target)
        open.update { it + entry }
        try {
            emitAll(engine.remoteSpeakers)
        } finally {
            open.update { list -> list.filterNot { it === entry } }
        }
    }

    init {
        // The open mic goes to the voice room when there is one, else only its level is read.
        // Closed, its level is still read (never sent) while the screen asks, to catch talking on mute.
        scope.launch {
            combine(micOpen, listenMuted, engine.connected) { o, l, c -> Triple(o, l, c) }.collect { (o, listen, connected) ->
                if (o && connected) {
                    mic.stop()
                    engine.setMicEnabled(true)
                } else {
                    engine.setMicEnabled(false)
                    if (o || listen) mic.start() else mic.stop()
                }
            }
        }
        scope.launch { follow() }
        scope.launch { prefetch() }
    }

    override fun setVoiceWanted(target: VoiceTarget, wanted: Boolean, talkers: Set<String>) {
        this.talkers.update { it + (target to talkers) }
        this.wanted.update { if (wanted) it + target else it - target }
    }

    /** Voice passes by target, fetched ahead (as a screen opens) so pressing the mic only has to connect. */
    private val passes = mutableMapOf<VoiceTarget, VoiceToken>()
    private val passLock = Mutex()

    /** A pass for [target] that is still good for a while: the one fetched ahead, else a fresh one. */
    private suspend fun pass(target: VoiceTarget): VoiceToken = passLock.withLock {
        val now = Clock.System.now().toEpochMilliseconds()
        passes[target]?.takeIf { it.expiresAt - now > PASS_MARGIN_MS } ?: tokens.token(target).also { passes[target] = it }
    }

    /** Fetches the pass of each screen that may talk as it opens (cheap, our own backend; no voice minutes). */
    private suspend fun prefetch() {
        activeTarget.collectLatest { target ->
            target ?: return@collectLatest
            try {
                pass(target)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                // Fetched again when the voice room is really needed.
            }
        }
    }

    /**
     * Keeps the device in the newest open screen's voice room, only while that screen wants it. A muted
     * mic keeps the room (the user still hears everyone); it is left only once there is nothing to send
     * or hear: mic off and speaker off, or mic off and every talker there muted on this phone.
     */
    private suspend fun follow() {
        var joinedRoom: String? = null
        combine(open.map { it.lastOrNull()?.target }, wanted, micOpen, talkers, muted) { target, want, micIsOpen, heard, mutedIds ->
            target?.takeIf { it in want && (micIsOpen || (heard[it].orEmpty() - mutedIds).isNotEmpty()) }
        }
            .distinctUntilChanged().collectLatest { target ->
            if (target == null) {
                // Stays a little after the last mic/speaker need ends: a screen handing over (lobby → match)
                // or a mic switched off and on again doesn't reconnect, and the wait stays short so no voice
                // minutes are spent on a silent room.
                delay(LINGER_MS)
                engine.disconnect()
                joinedRoom = null
                return@collectLatest
            }
            var backoff = RETRY_MIN_MS
            var healGap = HEAL_GAP_MIN_MS
            var reported = false
            while (true) {
                try {
                    val pass = pass(target)
                    val room = voiceRoomOf(pass.token)
                    if (!(engine.connected.value && room != null && room == joinedRoom)) {
                        joinedRoom = null
                        engine.connect(pass.url, pass.token)
                        joinedRoom = room
                    }
                    backoff = RETRY_MIN_MS
                    if (untilDroppedOrSomeoneUnheard(target)) {
                        // Someone talking isn't reaching this device: rejoin (spaced out, should it not help).
                        engine.disconnect()
                        joinedRoom = null
                        delay(healGap)
                        healGap = (healGap * 2).coerceAtMost(HEAL_GAP_MAX_MS)
                        continue
                    }
                    joinedRoom = null
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    if (!reported) CrashReporter.record(e, "VoiceClient.join")
                    reported = true
                }
                delay(backoff)
                backoff = (backoff * 2).coerceAtMost(RETRY_MAX_MS)
            }
        }
    }

    /**
     * Waits while in the voice room: false when it drops, true when someone there with their mic on has
     * gone unreceived for [UNHEARD_MS] (nobody this device muted, with the speaker on).
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun untilDroppedOrSomeoneUnheard(target: VoiceTarget): Boolean {
        val unheard = combine(talkers.map { it[target].orEmpty() }, engine.remoteAudio, muted, speaker) { expected, receiving, mutedIds, speakerOn ->
            speakerOn && (expected - mutedIds - receiving).isNotEmpty()
        }.distinctUntilChanged()
        return combine(engine.connected, unheard) { connected, missing -> connected to missing }
            .transformLatest { (connected, missing) ->
                when {
                    !connected -> emit(false)
                    missing -> {
                        delay(UNHEARD_MS)
                        emit(true)
                    }
                }
            }
            .first()
    }

    /** The voice room a LiveKit pass is for (its `video.room` claim). */
    @OptIn(ExperimentalEncodingApi::class)
    private fun voiceRoomOf(token: String): String? = runCatching {
        val payload = token.split('.')[1].let { it + "=".repeat((4 - it.length % 4) % 4) }
        val claims = json.parseToJsonElement(Base64.UrlSafe.decode(payload).decodeToString()).jsonObject
        claims["video"]?.jsonObject?.get("room")?.jsonPrimitive?.content
    }.getOrNull()

    private companion object {
        /** Stay in the voice room this long after it stops being needed (re-enabling the mic is then instant). */
        const val LINGER_MS = 15_000L
        /** A fetched pass is used only while it has at least this long left. */
        const val PASS_MARGIN_MS = 60_000L
        const val RETRY_MIN_MS = 2_000L
        const val RETRY_MAX_MS = 30_000L
        /** Someone with their mic on not received this long: rejoin. */
        const val UNHEARD_MS = 8_000L
        /** Waits after such a rejoin, doubling up to the max, so a talker who truly can't connect doesn't cause a loop. */
        const val HEAL_GAP_MIN_MS = 2_000L
        const val HEAL_GAP_MAX_MS = 300_000L
    }
}
