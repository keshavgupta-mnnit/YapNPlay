package com.kglabs28.yapnplay.appengine.games.doodleking.domain.usecase

import com.kglabs28.yapnplay.appengine.games.doodleking.domain.DoodleKingMatchRepository
import kotlinx.coroutines.flow.Flow

/** Who is talking at the table right now. */
class ObserveDoodleSpeakingUseCase(private val repository: DoodleKingMatchRepository) {
    operator fun invoke(matchId: String): Flow<Set<String>> = repository.observeSpeaking(matchId)
}
