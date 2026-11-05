package com.kglabs28.yapnplay.appengine.features.nearby.usecase

import com.kglabs28.yapnplay.appengine.features.nearby.domain.NearbyRadio
import com.kglabs28.yapnplay.appengine.features.nearby.repository.NearbyRepository

/** Opens the phone's settings for one switch Nearby play needs. */
class OpenNearbyRadioSettingsUseCase(private val repository: NearbyRepository) {
    operator fun invoke(radio: NearbyRadio) = repository.openRadioSettings(radio)
}
