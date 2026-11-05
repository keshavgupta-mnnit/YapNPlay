package com.kglabs28.yapnplay.appengine.features.nearby.usecase

import com.kglabs28.yapnplay.appengine.features.nearby.repository.NearbyRepository

/** False on devices with no Nearby radios. */
class IsNearbySupportedUseCase(private val repository: NearbyRepository) {
    operator fun invoke(): Boolean = repository.supported
}
