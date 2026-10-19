package com.kglabs28.yapnplay.appengine.games.monochaos.domain.usecase

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.games.monochaos.domain.MonoChaosCommand
import com.kglabs28.yapnplay.appengine.games.monochaos.domain.MonoChaosMatchRepository

class SendMonoChaosCommandUseCase(private val repository: MonoChaosMatchRepository) {
    suspend operator fun invoke(matchId: String, command: MonoChaosCommand): AppResult<Unit> =
        repository.send(matchId, command)
}
