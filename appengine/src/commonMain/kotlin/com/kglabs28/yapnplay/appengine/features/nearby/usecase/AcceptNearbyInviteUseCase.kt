package com.kglabs28.yapnplay.appengine.features.nearby.usecase

import com.kglabs28.yapnplay.appengine.features.nearby.repository.NearbyRepository

/** Accepts the invite from a phone nearby: this phone joins that party. */
class AcceptNearbyInviteUseCase(private val repository: NearbyRepository) {
    suspend operator fun invoke() = repository.acceptInvite()
}
