package com.kglabs28.yapnplay.appengine.features.voice.data

import com.kglabs28.yapnplay.appengine.features.voice.data.remote.VoiceClient
import com.kglabs28.yapnplay.appengine.features.voice.domain.VoiceRepository
import kotlinx.coroutines.flow.StateFlow

class VoiceRepositoryImpl(private val client: VoiceClient) : VoiceRepository {
    override val locallyMuted: StateFlow<Set<String>> get() = client.locallyMuted
    override fun setLocallyMuted(userId: String, muted: Boolean) = client.setLocallyMuted(userId, muted)
}
