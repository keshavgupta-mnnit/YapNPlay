package com.kglabs28.yapnplay.appengine.games.monochaos.domain.usecase

import com.kglabs28.yapnplay.appengine.games.monochaos.domain.MonoChaosMatchRepository
import com.kglabs28.yapnplay.appengine.games.monochaos.domain.MonoChaosUpdate
import kotlinx.coroutines.flow.Flow

class ObserveMonoChaosMatchUseCase(private val repository: MonoChaosMatchRepository) {
    suspend operator fun invoke(matchId: String): Flow<MonoChaosUpdate> = repository.observeMatch(matchId)
}
