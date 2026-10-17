package com.kglabs28.yapnplay.appengine.features.voice.usecase

import com.kglabs28.yapnplay.appengine.features.voice.domain.VoiceRepository

/** Turns the user's mic on or off everywhere: every room and game follows it. */
class SetMicOnUseCase(private val repository: VoiceRepository) {
    operator fun invoke(on: Boolean) = repository.setMicOn(on)
}
