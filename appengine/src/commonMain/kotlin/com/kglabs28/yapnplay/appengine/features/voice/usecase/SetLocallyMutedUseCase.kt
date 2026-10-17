package com.kglabs28.yapnplay.appengine.features.voice.usecase

import com.kglabs28.yapnplay.appengine.features.voice.domain.VoiceRepository

/** Mute or unmute [userId] on this device only. */
class SetLocallyMutedUseCase(private val repository: VoiceRepository) {
    operator fun invoke(userId: String, muted: Boolean) = repository.setLocallyMuted(userId, muted)
}
