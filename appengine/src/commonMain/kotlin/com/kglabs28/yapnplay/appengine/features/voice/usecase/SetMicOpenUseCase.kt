package com.kglabs28.yapnplay.appengine.features.voice.usecase

import com.kglabs28.yapnplay.appengine.features.voice.domain.VoiceRepository

/** Opens the user's mic while it's on at the table (and the app is in front), closes it otherwise; closed in front, it listens for talking on mute. */
class SetMicOpenUseCase(private val repository: VoiceRepository) {
    operator fun invoke(open: Boolean, listenMuted: Boolean) = repository.setMicOpen(open, listenMuted)
}
