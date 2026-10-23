package com.kglabs28.yapnplay.appengine.games.monochaos.domain.usecase

import com.kglabs28.yapnplay.appengine.games.monochaos.domain.MonoChaosMatchRepository
import kotlinx.coroutines.flow.Flow

/** User ids talking right now at the table. */
class ObserveMonoChaosSpeakingUseCase(private val repository: MonoChaosMatchRepository) {
    operator fun invoke(matchId: String): Flow<Set<String>> = repository.observeSpeaking(matchId)
}
