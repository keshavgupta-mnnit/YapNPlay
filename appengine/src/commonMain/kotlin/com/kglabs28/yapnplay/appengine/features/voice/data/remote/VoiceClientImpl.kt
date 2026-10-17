package com.kglabs28.yapnplay.appengine.features.voice.data.remote

import com.kglabs28.yapnplay.appengine.core.audio.MicLevelMonitor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * TODAY: remembers who is muted (no audio is playing yet).
 * LATER (voice provider, report Phase 13): also stop playing that
 * participant's audio on this device only, e.g.
 * `room.remoteParticipants[userId]?.audioTrackPublications?.forEach { it.setSubscribed(!muted) }`
 * — nothing is sent to the server and nobody else is affected.
 */
class VoiceClientImpl(private val mic: MicLevelMonitor) : VoiceClient {

    private val muted = MutableStateFlow<Set<String>>(emptySet())
    override val locallyMuted: StateFlow<Set<String>> = muted.asStateFlow()

    override fun setLocallyMuted(userId: String, muted: Boolean) {
        // --- production path ---
        // voiceRoom.remoteParticipants[userId]?.audioTrackPublications?.forEach { it.setSubscribed(!muted) }

        this.muted.update { if (muted) it + userId else it - userId }
    }

    private val micSwitch = MutableStateFlow(false)
    override val micOn: StateFlow<Boolean> = micSwitch.asStateFlow()

    override fun setMicOn(on: Boolean) {
        micSwitch.value = on
    }

    override val mySpeaking: StateFlow<Boolean> get() = mic.speaking

    override fun setMicOpen(open: Boolean) {
        // --- production path ---
        // voiceRoom.localParticipant.setMicrophoneEnabled(open) (its speaking events then drive mySpeaking)
        if (open) mic.start() else mic.stop()
    }
}
