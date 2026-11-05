package com.kglabs28.yapnplay.appengine.features.nearby.usecase

import com.kglabs28.yapnplay.appengine.features.nearby.domain.NearbyHostResult
import com.kglabs28.yapnplay.appengine.features.nearby.repository.NearbyRepository

/** Hosts a Quick Play party on this phone for Nearby play: its room, or why the radios wouldn't start. */
class HostNearbyPartyUseCase(private val repository: NearbyRepository) {
    suspend operator fun invoke(): NearbyHostResult = repository.host()
}
