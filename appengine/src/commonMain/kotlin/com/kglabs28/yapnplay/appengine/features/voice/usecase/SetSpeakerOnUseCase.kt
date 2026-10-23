package com.kglabs28.yapnplay.appengine.features.voice.usecase

import com.kglabs28.yapnplay.appengine.features.voice.domain.VoiceRepository

/** Turns this device's speaker on or off: off, the user hears nobody (only on their device). */
class SetSpeakerOnUseCase(private val repository: VoiceRepository) {
    operator fun invoke(on: Boolean) = repository.setSpeakerOn(on)
}
