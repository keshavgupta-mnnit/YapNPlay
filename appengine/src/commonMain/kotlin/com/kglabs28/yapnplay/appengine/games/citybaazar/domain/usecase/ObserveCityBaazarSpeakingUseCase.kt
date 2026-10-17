package com.kglabs28.yapnplay.appengine.games.citybaazar.domain.usecase

import com.kglabs28.yapnplay.appengine.games.citybaazar.domain.CityBaazarMatchRepository
import kotlinx.coroutines.flow.Flow

/** Who is talking at the table right now. */
class ObserveCityBaazarSpeakingUseCase(private val repository: CityBaazarMatchRepository) {
    operator fun invoke(matchId: String): Flow<Set<String>> = repository.observeSpeaking(matchId)
}
