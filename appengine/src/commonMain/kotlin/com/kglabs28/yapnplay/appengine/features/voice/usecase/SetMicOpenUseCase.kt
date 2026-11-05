package com.kglabs28.yapnplay.appengine.features.voice.usecase

import com.kglabs28.yapnplay.appengine.features.voice.domain.VoiceRepository

/**
 * Opens the user's mic at one lobby or table while it's on there (and the app is in front), closes it
 * otherwise; closed in front, it listens for talking on mute. Each screen keeps its own state.
 */
class SetMicOpenUseCase(private val repository: VoiceRepository) {
    fun room(roomId: String, open: Boolean, listenMuted: Boolean) = repository.setRoomMicOpen(roomId, open, listenMuted)
    fun match(matchId: String, open: Boolean, listenMuted: Boolean) = repository.setMatchMicOpen(matchId, open, listenMuted)
}
