package com.kglabs28.yapnplay.appengine.features.nearby.usecase

import com.kglabs28.yapnplay.appengine.features.nearby.repository.NearbyRepository

/** Leaves the party (the host ends it for everyone) and goes back online. */
class LeaveNearbyUseCase(private val repository: NearbyRepository) {
    suspend operator fun invoke() = repository.leave()

    /** The screen is gone: finish leaving in the background. */
    fun inBackground() = repository.leaveInBackground()
}
