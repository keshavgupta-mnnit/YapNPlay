package com.kglabs28.yapnplay.appengine.games.mono.domain.usecase

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.games.mono.domain.MonoCommand
import com.kglabs28.yapnplay.appengine.games.mono.domain.MonoMatchRepository

class SendMonoCommandUseCase(private val repository: MonoMatchRepository) {
    suspend operator fun invoke(matchId: String, command: MonoCommand): AppResult<Unit> =
        repository.send(matchId, command)
}
