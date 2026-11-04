package com.kglabs28.yapnplay.appengine.games.doodleking.domain.usecase

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.games.doodleking.domain.DoodleInkOp
import com.kglabs28.yapnplay.appengine.games.doodleking.domain.DoodleKingMatchRepository

/** The user's drawing as they draw it, and asking for the whole drawing. */
class SendDoodleInkUseCase(private val repository: DoodleKingMatchRepository) {
    suspend operator fun invoke(matchId: String, op: DoodleInkOp): AppResult<Unit> = repository.sendInk(matchId, op)
    suspend fun requestDrawing(matchId: String): AppResult<Unit> = repository.requestDrawing(matchId)
}
