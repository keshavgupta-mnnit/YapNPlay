package com.kglabs28.yapnplay.appengine.games.royalludo.domain.usecase

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.games.royalludo.domain.LudoCommand
import com.kglabs28.yapnplay.appengine.games.royalludo.domain.LudoMatchRepository

/** Roll, move, autoplay, mic or leave. The server checks it is allowed. */
class SendLudoCommandUseCase(private val repository: LudoMatchRepository) {
    suspend operator fun invoke(matchId: String, command: LudoCommand): AppResult<Unit> =
        repository.send(matchId, command)
}
