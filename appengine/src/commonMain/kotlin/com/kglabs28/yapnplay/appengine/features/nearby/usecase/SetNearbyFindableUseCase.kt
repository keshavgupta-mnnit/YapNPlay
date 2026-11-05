package com.kglabs28.yapnplay.appengine.features.nearby.usecase

import com.kglabs28.yapnplay.appengine.features.nearby.repository.NearbyRepository

/** Lets phones around find this one (app in front), or stops (app in the background). */
class SetNearbyFindableUseCase(private val repository: NearbyRepository) {
    suspend operator fun invoke(findable: Boolean) = if (findable) repository.becomeFindable() else repository.stopBeingFindable()
}
