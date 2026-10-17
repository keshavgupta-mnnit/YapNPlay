package com.kglabs28.yapnplay.appengine.games.citybaazar.domain.usecase

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.games.citybaazar.domain.CityBaazarCommand
import com.kglabs28.yapnplay.appengine.games.citybaazar.domain.CityBaazarMatchRepository

/** Sends one of the player's actions to the match. */
class SendCityBaazarCommandUseCase(private val repository: CityBaazarMatchRepository) {
    suspend operator fun invoke(matchId: String, command: CityBaazarCommand): AppResult<Unit> = repository.send(matchId, command)
}
