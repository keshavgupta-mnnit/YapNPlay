package com.kglabs28.yapnplay.appengine.features.voice.usecase

import com.kglabs28.yapnplay.appengine.features.voice.domain.VoiceRepository

/**
 * Tells voice whether a lobby or table needs its voice room right now (see VoiceNeed); without it the
 * device stays out. [talkers]: the others there with their mic on, who should be heard.
 */
class SetVoiceWantedUseCase(private val repository: VoiceRepository) {
    fun room(roomId: String, wanted: Boolean, talkers: Set<String>) = repository.setRoomVoiceWanted(roomId, wanted, talkers)
    fun match(matchId: String, wanted: Boolean, talkers: Set<String>) = repository.setMatchVoiceWanted(matchId, wanted, talkers)
}
