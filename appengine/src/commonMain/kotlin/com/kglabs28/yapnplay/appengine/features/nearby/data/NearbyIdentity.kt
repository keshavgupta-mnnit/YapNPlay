package com.kglabs28.yapnplay.appengine.features.nearby.data

import com.kglabs28.yapnplay.appengine.core.network.CredentialStore
import com.kglabs28.yapnplay.appengine.core.network.DeviceIdentity
import com.kglabs28.yapnplay.appengine.core.ui.NearbyStrings
import com.kglabs28.yapnplay.appengine.features.nearby.domain.NearbyPlayer
import com.kglabs28.yapnplay.appengine.features.profile.usecase.ObserveMyProfileUseCase
import kotlin.random.Random

/**
 * Who this phone is at a Nearby party, with no server to ask: the saved
 * account id (or the phone's own id), and the profile's name and avatar
 * when they were loaded before the internet went.
 */
class NearbyIdentity(
    private val credentials: CredentialStore,
    private val device: DeviceIdentity,
    private val profile: ObserveMyProfileUseCase,
) {
    /** Kept for the app's run, so a phone with no account is still one person all along. */
    private val fallbackId: String by lazy { FALLBACK_PREFIX + Random.nextLong().toULong().toString(ID_RADIX) }

    fun me(): NearbyPlayer {
        val known = profile().value
        val userId = credentials.load()?.userId ?: device.deviceId() ?: fallbackId
        return NearbyPlayer(
            userId = userId,
            name = known?.name?.takeIf { it.isNotBlank() } ?: NearbyStrings.defaultName,
            avatarId = known?.avatarId.orEmpty(),
        )
    }

    private companion object {
        const val FALLBACK_PREFIX = "phone-"
        const val ID_RADIX = 36
    }
}
