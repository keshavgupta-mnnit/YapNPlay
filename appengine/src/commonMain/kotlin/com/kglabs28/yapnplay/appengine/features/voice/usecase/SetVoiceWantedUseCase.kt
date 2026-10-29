package com.kglabs28.yapnplay.appengine.features.voice.usecase

import com.kglabs28.yapnplay.appengine.features.voice.domain.VoiceRepository

/** Tells voice whether a lobby or table needs its voice room right now (see VoiceNeed); without it the device stays out. */
class SetVoiceWantedUseCase(private val repository: VoiceRepository) {
    fun room(roomId: String, wanted: Boolean) = repository.setRoomVoiceWanted(roomId, wanted)
    fun match(matchId: String, wanted: Boolean) = repository.setMatchVoiceWanted(matchId, wanted)
}
