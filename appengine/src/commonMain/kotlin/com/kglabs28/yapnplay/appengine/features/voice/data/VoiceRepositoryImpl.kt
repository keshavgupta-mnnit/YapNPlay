package com.kglabs28.yapnplay.appengine.features.voice.data

import com.kglabs28.yapnplay.appengine.features.voice.domain.MySpeech
import com.kglabs28.yapnplay.appengine.features.voice.data.remote.VoiceClient
import com.kglabs28.yapnplay.appengine.features.voice.data.remote.VoiceTarget
import com.kglabs28.yapnplay.appengine.features.voice.domain.VoiceRepository
import kotlinx.coroutines.flow.StateFlow

class VoiceRepositoryImpl(private val client: VoiceClient) : VoiceRepository {
    override val locallyMuted: StateFlow<Set<String>> get() = client.locallyMuted
    override fun setLocallyMuted(userId: String, muted: Boolean) = client.setLocallyMuted(userId, muted)
    override val speakerOn: StateFlow<Boolean> get() = client.speakerOn
    override fun setSpeakerOn(on: Boolean) = client.setSpeakerOn(on)
    override val micOn: StateFlow<Boolean> get() = client.micOn
    override fun setMicOn(on: Boolean) = client.setMicOn(on)
    override val mySpeech: StateFlow<MySpeech> get() = client.mySpeech
    override fun setMicOpen(open: Boolean, listenMuted: Boolean) = client.setMicOpen(open, listenMuted)
    override fun setRoomVoiceWanted(roomId: String, wanted: Boolean, talkers: Set<String>) = client.setVoiceWanted(VoiceTarget.Room(roomId), wanted, talkers)
    override fun setMatchVoiceWanted(matchId: String, wanted: Boolean, talkers: Set<String>) = client.setVoiceWanted(VoiceTarget.Match(matchId), wanted, talkers)
}
