package com.kglabs28.yapnplay.appengine.games.doodleking.domain.usecase

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.games.doodleking.domain.DoodleCommand
import com.kglabs28.yapnplay.appengine.games.doodleking.domain.DoodleKingMatchRepository

/** The user's moves: pick a word, guess, pass, mic, leave. */
class SendDoodleCommandUseCase(private val repository: DoodleKingMatchRepository) {
    suspend operator fun invoke(matchId: String, command: DoodleCommand): AppResult<Unit> = repository.send(matchId, command)
}
