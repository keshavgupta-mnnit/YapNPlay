package com.kglabs28.yapnplay.appengine.games.citybazaar.domain.usecase

import com.kglabs28.yapnplay.appengine.games.citybazaar.domain.CityBazaarMatchRepository
import kotlinx.coroutines.flow.Flow

/** Who is talking at the table right now. */
class ObserveCityBazaarSpeakingUseCase(private val repository: CityBazaarMatchRepository) {
    operator fun invoke(matchId: String): Flow<Set<String>> = repository.observeSpeaking(matchId)
}
