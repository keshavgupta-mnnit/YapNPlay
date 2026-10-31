package com.kglabs28.yapnplay.appengine.features.voice.domain

import kotlinx.coroutines.flow.StateFlow

/** Voice controls on this device: who the user hears (each person, or nobody), and their own mic. */
interface VoiceRepository {
    val locallyMuted: StateFlow<Set<String>>
    fun setLocallyMuted(userId: String, muted: Boolean)
    val speakerOn: StateFlow<Boolean>
    fun setSpeakerOn(on: Boolean)
    val micOn: StateFlow<Boolean>
    fun setMicOn(on: Boolean)
    val mySpeech: StateFlow<MySpeech>
    fun setMicOpen(open: Boolean, listenMuted: Boolean)
    fun setRoomVoiceWanted(roomId: String, wanted: Boolean)
    fun setMatchVoiceWanted(matchId: String, wanted: Boolean)
}
