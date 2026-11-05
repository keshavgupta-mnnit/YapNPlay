package com.kglabs28.yapnplay.appengine.features.nearby.usecase

import com.kglabs28.yapnplay.appengine.features.nearby.repository.NearbyRepository

/** True when the room is the Nearby party this phone is in (not an online room). */
class IsNearbyRoomUseCase(private val repository: NearbyRepository) {
    operator fun invoke(roomId: String): Boolean = repository.isNearbyRoom(roomId)
}
