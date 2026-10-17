package com.kglabs28.yapnplay.appengine.games.citybazaar.domain.usecase

import com.kglabs28.yapnplay.appengine.games.citybazaar.domain.CityBazaarMatchRepository
import com.kglabs28.yapnplay.appengine.games.citybazaar.domain.CityBazaarUpdate
import kotlinx.coroutines.flow.Flow

/** The match as it changes, each update with the events to animate. */
class ObserveCityBazaarMatchUseCase(private val repository: CityBazaarMatchRepository) {
    operator fun invoke(matchId: String): Flow<CityBazaarUpdate> = repository.observeMatch(matchId)
}
