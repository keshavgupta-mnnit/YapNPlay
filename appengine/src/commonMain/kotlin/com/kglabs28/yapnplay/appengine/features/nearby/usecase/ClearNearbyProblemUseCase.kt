package com.kglabs28.yapnplay.appengine.features.nearby.usecase

import com.kglabs28.yapnplay.appengine.features.nearby.repository.NearbyRepository

/** Clears the problem just shown. */
class ClearNearbyProblemUseCase(private val repository: NearbyRepository) {
    operator fun invoke() = repository.clearProblem()
}
