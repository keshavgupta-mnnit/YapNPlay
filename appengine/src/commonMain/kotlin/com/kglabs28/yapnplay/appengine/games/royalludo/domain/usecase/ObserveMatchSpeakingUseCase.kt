package com.kglabs28.yapnplay.appengine.games.royalludo.domain.usecase

import com.kglabs28.yapnplay.appengine.games.royalludo.domain.LudoMatchRepository
import kotlinx.coroutines.flow.Flow

/** User ids talking right now at the table. */
class ObserveMatchSpeakingUseCase(private val repository: LudoMatchRepository) {
    operator fun invoke(matchId: String): Flow<Set<String>> = repository.observeSpeaking(matchId)
}
