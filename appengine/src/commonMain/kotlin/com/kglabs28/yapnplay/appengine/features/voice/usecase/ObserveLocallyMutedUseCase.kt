package com.kglabs28.yapnplay.appengine.features.voice.usecase

import com.kglabs28.yapnplay.appengine.features.voice.domain.VoiceRepository
import kotlinx.coroutines.flow.StateFlow

/** People the user has muted for themselves (others still hear them). */
class ObserveLocallyMutedUseCase(private val repository: VoiceRepository) {
    operator fun invoke(): StateFlow<Set<String>> = repository.locallyMuted
}
