package com.kglabs28.yapnplay.appengine.features.voice.usecase

import com.kglabs28.yapnplay.appengine.features.voice.domain.VoiceRepository
import kotlinx.coroutines.flow.StateFlow

/** Whether the user is speaking into their open mic right now. */
class ObserveMySpeakingUseCase(private val repository: VoiceRepository) {
    operator fun invoke(): StateFlow<Boolean> = repository.mySpeaking
}
