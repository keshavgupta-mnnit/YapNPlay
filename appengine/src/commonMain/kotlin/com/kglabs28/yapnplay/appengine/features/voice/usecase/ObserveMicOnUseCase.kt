package com.kglabs28.yapnplay.appengine.features.voice.usecase

import com.kglabs28.yapnplay.appengine.features.voice.domain.VoiceRepository
import kotlinx.coroutines.flow.StateFlow

/** The user's mic switch, shared by every room and game. */
class ObserveMicOnUseCase(private val repository: VoiceRepository) {
    operator fun invoke(): StateFlow<Boolean> = repository.micOn
}
