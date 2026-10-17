package com.kglabs28.yapnplay.appengine.games.royalludo.domain.usecase

import com.kglabs28.yapnplay.appengine.games.royalludo.domain.LudoMatchRepository
import kotlinx.coroutines.flow.Flow

/** Live round-trip time to the server in ms, for the ping in the top bar. */
class ObserveConnectionLatencyUseCase(private val repository: LudoMatchRepository) {
    operator fun invoke(): Flow<Int> = repository.observeLatency()
}
