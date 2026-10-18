package com.kglabs28.yapnplay.appengine.features.voice

import android.content.Context
import com.kglabs28.yapnplay.appengine.core.common.CrashReporter
import com.kglabs28.yapnplay.appengine.features.voice.data.remote.VoiceEngine
import io.livekit.android.LiveKit
import io.livekit.android.events.RoomEvent
import io.livekit.android.room.Room
import io.livekit.android.room.participant.LocalParticipant
import io.livekit.android.room.track.RemoteTrackPublication
import io.livekit.android.room.track.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

/**
 * LiveKit voice: one [Room] per voice room joined, released on leave.
 * Everyone's identity is their user id (the backend's token says so).
 * Muting someone here unsubscribes from their audio, so this device
 * neither downloads nor plays it; nobody else is affected.
 */
class LiveKitVoiceEngine(private val context: Context) : VoiceEngine {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val micLock = Mutex()

    private var room: Room? = null
    private var events: Job? = null
    private var micWanted = false
    private var muted: Set<String> = emptySet()

    private val isConnected = MutableStateFlow(false)
    override val connected: StateFlow<Boolean> = isConnected.asStateFlow()

    private val others = MutableStateFlow<Set<String>>(emptySet())
    override val remoteSpeakers: StateFlow<Set<String>> = others.asStateFlow()

    private val me = MutableStateFlow(false)
    override val localSpeaking: StateFlow<Boolean> = me.asStateFlow()

    override suspend fun connect(url: String, token: String) = withContext(Dispatchers.Main) {
        leave()
        val next = LiveKit.create(context.applicationContext)
        room = next
        events = scope.launch { next.events.events.collect { onEvent(next, it) } }
        try {
            next.connect(url, token)
        } catch (e: Throwable) {
            leave()
            throw e
        }
        isConnected.value = true
        applyMutes(next)
        applyMic()
    }

    override suspend fun disconnect() = withContext(Dispatchers.Main) { leave() }

    override fun setMicEnabled(enabled: Boolean) {
        micWanted = enabled
        applyMic()
    }

    override fun setMuted(userIds: Set<String>) {
        muted = userIds
        scope.launch { room?.let(::applyMutes) }
    }

    private fun applyMic() {
        scope.launch {
            micLock.withLock {
                val current = room?.takeIf { isConnected.value } ?: return@withLock
                runCatching { current.localParticipant.setMicrophoneEnabled(micWanted) }
                    .onFailure { CrashReporter.record(it, "LiveKitVoiceEngine.mic") }
            }
        }
    }

    private fun onEvent(source: Room, event: RoomEvent) {
        if (source !== room) return
        when (event) {
            is RoomEvent.ActiveSpeakersChanged -> {
                others.value = event.speakers.filterNot { it is LocalParticipant }.mapNotNull { it.identity?.value }.toSet()
                me.value = event.speakers.any { it is LocalParticipant }
            }
            is RoomEvent.TrackPublished, is RoomEvent.TrackSubscribed, is RoomEvent.ParticipantConnected -> applyMutes(source)
            is RoomEvent.Disconnected -> leave()
            else -> Unit
        }
    }

    private fun applyMutes(source: Room) {
        source.remoteParticipants.values.forEach { participant ->
            val hear = participant.identity?.value !in muted
            participant.trackPublications.values
                .filterIsInstance<RemoteTrackPublication>()
                .filter { it.kind == Track.Kind.AUDIO && it.isDesired != hear }
                .forEach { it.setSubscribed(hear) }
        }
    }

    private fun leave() {
        events?.cancel()
        events = null
        room?.let {
            room = null
            runCatching { it.disconnect() }
            runCatching { it.release() }
        }
        isConnected.value = false
        others.value = emptySet()
        me.value = false
    }
}
