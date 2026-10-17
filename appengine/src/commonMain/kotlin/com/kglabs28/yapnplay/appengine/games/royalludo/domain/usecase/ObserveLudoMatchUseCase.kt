package com.kglabs28.yapnplay.appengine.games.royalludo.domain.usecase

import com.kglabs28.yapnplay.appengine.games.royalludo.domain.LudoUpdate
import com.kglabs28.yapnplay.appengine.games.royalludo.domain.LudoMatchRepository
import kotlinx.coroutines.flow.Flow

/** The match as it changes, each update with the events to animate. */
class ObserveLudoMatchUseCase(private val repository: LudoMatchRepository) {
    operator fun invoke(matchId: String): Flow<LudoUpdate> = repository.observeMatch(matchId)
}
