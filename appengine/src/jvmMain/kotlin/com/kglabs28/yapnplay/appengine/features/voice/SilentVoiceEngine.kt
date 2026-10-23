package com.kglabs28.yapnplay.appengine.features.voice

import com.kglabs28.yapnplay.appengine.features.voice.data.remote.VoiceEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/** Desktop has no voice: never connects, nobody speaks. */
class SilentVoiceEngine : VoiceEngine {
    override val connected: StateFlow<Boolean> = MutableStateFlow(false)
    override val remoteSpeakers: StateFlow<Set<String>> = MutableStateFlow(emptySet())
    override val localSpeaking: StateFlow<Boolean> = MutableStateFlow(false)
    override suspend fun connect(url: String, token: String) = error("voice is not available on desktop")
    override suspend fun disconnect() = Unit
    override fun setMicEnabled(enabled: Boolean) = Unit
    override fun setMuted(userIds: Set<String>) = Unit
    override fun setSpeakerOn(on: Boolean) = Unit
}
