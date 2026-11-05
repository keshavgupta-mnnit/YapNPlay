package com.kglabs28.yapnplay.appengine.features.nearby.usecase

import com.kglabs28.yapnplay.appengine.features.nearby.domain.NearbyState
import com.kglabs28.yapnplay.appengine.features.nearby.repository.NearbyRepository
import kotlinx.coroutines.flow.StateFlow

/** The Nearby party as it is now, and every change. */
class ObserveNearbyUseCase(private val repository: NearbyRepository) {
    operator fun invoke(): StateFlow<NearbyState> = repository.state
}
