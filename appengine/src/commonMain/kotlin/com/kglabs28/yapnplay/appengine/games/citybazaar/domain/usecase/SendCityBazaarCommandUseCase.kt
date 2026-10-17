package com.kglabs28.yapnplay.appengine.games.citybazaar.domain.usecase

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.games.citybazaar.domain.CityBazaarCommand
import com.kglabs28.yapnplay.appengine.games.citybazaar.domain.CityBazaarMatchRepository

/** Sends one of the player's actions to the match. */
class SendCityBazaarCommandUseCase(private val repository: CityBazaarMatchRepository) {
    suspend operator fun invoke(matchId: String, command: CityBazaarCommand): AppResult<Unit> = repository.send(matchId, command)
}
