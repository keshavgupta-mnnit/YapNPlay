package com.kglabs28.yapnplay.appengine.games.citybaazar.domain.usecase

import com.kglabs28.yapnplay.appengine.games.citybaazar.domain.CityBaazarMatchRepository
import com.kglabs28.yapnplay.appengine.games.citybaazar.domain.CityBaazarUpdate
import kotlinx.coroutines.flow.Flow

/** The match as it changes, each update with the events to animate. */
class ObserveCityBaazarMatchUseCase(private val repository: CityBaazarMatchRepository) {
    operator fun invoke(matchId: String): Flow<CityBaazarUpdate> = repository.observeMatch(matchId)
}
