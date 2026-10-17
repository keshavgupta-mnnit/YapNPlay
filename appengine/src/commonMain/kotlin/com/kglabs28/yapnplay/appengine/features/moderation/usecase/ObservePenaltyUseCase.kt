package com.kglabs28.yapnplay.appengine.features.moderation.usecase

import com.kglabs28.yapnplay.appengine.features.moderation.domain.ModerationRepository
import com.kglabs28.yapnplay.appengine.features.moderation.domain.Penalty
import kotlinx.coroutines.flow.StateFlow

/** The user's current penalty (play blocked until a time), or null. */
class ObservePenaltyUseCase(private val repository: ModerationRepository) {
    operator fun invoke(): StateFlow<Penalty?> = repository.penalty
}
