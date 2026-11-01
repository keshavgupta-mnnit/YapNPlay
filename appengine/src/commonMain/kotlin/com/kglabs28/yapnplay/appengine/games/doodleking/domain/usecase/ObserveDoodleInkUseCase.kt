package com.kglabs28.yapnplay.appengine.games.doodleking.domain.usecase

import com.kglabs28.yapnplay.appengine.games.doodleking.domain.DoodleInkOp
import com.kglabs28.yapnplay.appengine.games.doodleking.domain.DoodleKingMatchRepository
import kotlinx.coroutines.flow.Flow

/** The drawer's strokes as they come in (and the whole drawing when asked for). */
class ObserveDoodleInkUseCase(private val repository: DoodleKingMatchRepository) {
    operator fun invoke(): Flow<DoodleInkOp> = repository.observeInk()
}
