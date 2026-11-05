package com.kglabs28.yapnplay.appengine.features.nearby.usecase

import com.kglabs28.yapnplay.appengine.features.nearby.domain.NearbyRadio
import com.kglabs28.yapnplay.appengine.features.nearby.repository.NearbyRepository

/** Each switch Nearby play needs on this phone (Bluetooth, Wi-Fi, Location on older phones) and whether it is on. */
class GetNearbyRadiosUseCase(private val repository: NearbyRepository) {
    operator fun invoke(): Map<NearbyRadio, Boolean> = repository.radios()
}
