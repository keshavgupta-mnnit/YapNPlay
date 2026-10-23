package com.kglabs28.yapnplay.appengine.games.mono.domain.usecase

import com.kglabs28.yapnplay.appengine.games.mono.domain.MonoMatchRepository
import com.kglabs28.yapnplay.appengine.games.mono.domain.MonoUpdate
import kotlinx.coroutines.flow.Flow

class ObserveMonoMatchUseCase(private val repository: MonoMatchRepository) {
    suspend operator fun invoke(matchId: String): Flow<MonoUpdate> = repository.observeMatch(matchId)
}
