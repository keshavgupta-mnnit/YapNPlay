package com.kglabs28.yapnplay.appengine.features.nearby.usecase

import com.kglabs28.yapnplay.appengine.features.nearby.repository.NearbyRepository

/** Host: invites a phone around to the party. */
class InviteNearbyPlayerUseCase(private val repository: NearbyRepository) {
    suspend operator fun invoke(endpointId: String) = repository.invite(endpointId)
}
