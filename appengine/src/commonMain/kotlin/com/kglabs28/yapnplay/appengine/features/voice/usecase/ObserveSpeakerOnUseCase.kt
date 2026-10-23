package com.kglabs28.yapnplay.appengine.features.voice.usecase

import com.kglabs28.yapnplay.appengine.features.voice.domain.VoiceRepository
import kotlinx.coroutines.flow.StateFlow

/** Whether this device plays the others' voices (the user's speaker switch). */
class ObserveSpeakerOnUseCase(private val repository: VoiceRepository) {
    operator fun invoke(): StateFlow<Boolean> = repository.speakerOn
}
