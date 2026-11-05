package com.kglabs28.yapnplay.appengine.features.nearby.usecase

import com.kglabs28.yapnplay.appengine.features.nearby.repository.NearbyRepository

/** Turns down the invite from a phone nearby. */
class DeclineNearbyInviteUseCase(private val repository: NearbyRepository) {
    suspend operator fun invoke() = repository.declineInvite()
}
