package com.kglabs28.yapnplay.appengine.features.voice.usecase

import com.kglabs28.yapnplay.appengine.features.voice.domain.MySpeech
import com.kglabs28.yapnplay.appengine.features.voice.domain.VoiceRepository
import kotlinx.coroutines.flow.StateFlow

/** Whether the user is speaking right now: into their open mic, or with it off. */
class ObserveMySpeakingUseCase(private val repository: VoiceRepository) {
    operator fun invoke(): StateFlow<MySpeech> = repository.mySpeech
}
