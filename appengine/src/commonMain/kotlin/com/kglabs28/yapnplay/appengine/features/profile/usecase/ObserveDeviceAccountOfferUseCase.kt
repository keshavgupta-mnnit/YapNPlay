package com.kglabs28.yapnplay.appengine.features.profile.usecase

import com.kglabs28.yapnplay.appengine.core.network.BackendSession
import com.kglabs28.yapnplay.appengine.core.network.DeviceAccountOffer
import kotlinx.coroutines.flow.StateFlow

/** An account already made on this phone, offered back on the first launch; null when there is none to decide on. */
class ObserveDeviceAccountOfferUseCase(private val session: BackendSession) {
    operator fun invoke(): StateFlow<DeviceAccountOffer?> = session.deviceAccountOffer
}
