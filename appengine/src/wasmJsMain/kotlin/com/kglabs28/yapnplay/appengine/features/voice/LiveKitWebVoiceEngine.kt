package com.kglabs28.yapnplay.appengine.features.voice

import com.kglabs28.yapnplay.appengine.core.common.CrashReporter
import com.kglabs28.yapnplay.appengine.features.voice.data.remote.VoiceEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.await
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Web voice over LiveKit's browser SDK ([liveKitConnect] and friends), the
 * same rules as the Android engine: identities are user ids; muting someone
 * unsubscribes from their audio; a wanted mic is published again (right
 * after joining, after a reconnect, and if it is ever lost). iPhone Safari
 * starts the sound on the player's first tap after joining.
 */
class LiveKitWebVoiceEngine : VoiceEngine {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val micLock = Mutex()
    private var micWatch: Job? = null
    private var micWanted = false
    private var muted: Set<String> = emptySet()
    private var speakerOn = true

    private val isConnected = MutableStateFlow(false)
    override val connected: StateFlow<Boolean> = isConnected.asStateFlow()

    private val others = MutableStateFlow<Set<String>>(emptySet())
    override val remoteSpeakers: StateFlow<Set<String>> = others.asStateFlow()

    private val receiving = MutableStateFlow<Set<String>>(emptySet())
    override val remoteAudio: StateFlow<Set<String>> = receiving.asStateFlow()

    private val me = MutableStateFlow(false)
    override val localSpeaking: StateFlow<Boolean> = me.asStateFlow()

    override suspend fun connect(url: String, token: String) {
        leave()
        applyListening()
        try {
            liveKitConnect(url, token, ::onEvent).await<JsAny?>()
        } catch (e: Throwable) {
            leave()
            throw e
        }
        isConnected.value = true
        applyMic()
        micWatch = scope.launch {
            while (true) {
                delay(MIC_CHECK_MS)
                republishLostMic()
            }
        }
    }

    override suspend fun disconnect() = leave()

    override fun setMicEnabled(enabled: Boolean) {
        micWanted = enabled
        applyMic()
    }

    override fun setMuted(userIds: Set<String>) {
        muted = userIds
        applyListening()
    }

    override fun setSpeakerOn(on: Boolean) {
        speakerOn = on
        applyListening()
    }

    private fun applyListening() = liveKitSetListening(muted.joinToString(LIST_SEPARATOR), speakerOn)

    private fun onEvent(event: String, value: String) {
        when (event) {
            EVENT_SPEAKERS -> others.value = idsOf(value)
            EVENT_ME -> me.value = value == TRUE
            EVENT_RECEIVING -> receiving.value = idsOf(value)
            EVENT_RECONNECTED -> scope.launch { republishLostMic() }
            EVENT_DISCONNECTED -> scope.launch { leave() }
        }
    }

    /** On: published, tried again a few times while the room finishes setting up. Off: the track is only muted. */
    private fun applyMic() {
        scope.launch {
            micLock.withLock {
                if (!isConnected.value) return@withLock
                if (micWanted) publishMic() else setMic(false, "muteMic")
            }
        }
    }

    private suspend fun publishMic() {
        var last: Throwable? = null
        repeat(MIC_TRIES) {
            if (!micWanted || !isConnected.value) return
            val result = runCatching { liveKitSetMic(true).await<JsAny?>() }
            if (result.isSuccess) return
            last = result.exceptionOrNull()
            delay(MIC_RETRY_MS)
        }
        last?.let { CrashReporter.record(it, "LiveKitWebVoiceEngine.mic") }
    }

    private suspend fun setMic(enabled: Boolean, context: String) {
        runCatching { liveKitSetMic(enabled).await<JsAny?>() }
            .onFailure { CrashReporter.record(it, "LiveKitWebVoiceEngine.$context") }
    }

    /** The mic is wanted but not going out: publish it again. */
    private suspend fun republishLostMic() {
        micLock.withLock {
            if (!isConnected.value || !micWanted || liveKitMicLive()) return@withLock
            setMic(false, "republishMic")
            setMic(true, "republishMic")
        }
    }

    private suspend fun leave() {
        micWatch?.cancel()
        micWatch = null
        runCatching { liveKitDisconnect().await<JsAny?>() }
        isConnected.value = false
        others.value = emptySet()
        receiving.value = emptySet()
        me.value = false
    }

    private fun idsOf(value: String): Set<String> = value.split(LIST_SEPARATOR).filter { it.isNotEmpty() }.toSet()

    private companion object {
        const val LIST_SEPARATOR = ","
        const val TRUE = "1"
        const val EVENT_SPEAKERS = "speakers"
        const val EVENT_ME = "me"
        const val EVENT_RECEIVING = "receiving"
        const val EVENT_RECONNECTED = "reconnected"
        const val EVENT_DISCONNECTED = "disconnected"
        /** How often a wanted mic is checked to really be going out. */
        const val MIC_CHECK_MS = 5_000L
        /** Publishing the mic right after joining: tries, and the pause between them. */
        const val MIC_TRIES = 6
        const val MIC_RETRY_MS = 250L
    }
}
