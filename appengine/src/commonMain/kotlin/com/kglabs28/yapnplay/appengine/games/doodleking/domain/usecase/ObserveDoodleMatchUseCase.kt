package com.kglabs28.yapnplay.appengine.games.doodleking.domain.usecase

import com.kglabs28.yapnplay.appengine.games.doodleking.domain.DoodleKingMatchRepository
import com.kglabs28.yapnplay.appengine.games.doodleking.domain.DoodleUpdate
import kotlinx.coroutines.flow.Flow

/** The match as it changes. */
class ObserveDoodleMatchUseCase(private val repository: DoodleKingMatchRepository) {
    operator fun invoke(matchId: String): Flow<DoodleUpdate> = repository.observeMatch(matchId)
}
