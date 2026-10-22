package com.kglabs28.yapnplay.appengine.games.mono.domain.usecase

import com.kglabs28.yapnplay.appengine.games.mono.domain.MonoMatchRepository
import kotlinx.coroutines.flow.Flow

/** User ids talking right now at the table. */
class ObserveMonoSpeakingUseCase(private val repository: MonoMatchRepository) {
    operator fun invoke(matchId: String): Flow<Set<String>> = repository.observeSpeaking(matchId)
}
