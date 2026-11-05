package com.kglabs28.yapnplay.appengine.features.nearby.usecase

import com.kglabs28.yapnplay.appengine.features.nearby.repository.NearbyRepository

/** Hosts a Quick Play party on this phone for Nearby play: its room id, or null when the radios failed. */
class HostNearbyPartyUseCase(private val repository: NearbyRepository) {
    suspend operator fun invoke(): String? = repository.host()
}
