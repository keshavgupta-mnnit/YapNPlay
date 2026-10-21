package com.kglabs28.yapnplay.appengine.features.playrooms.usecase

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.features.profile.repository.ProfileRepository
import com.kglabs28.yapnplay.appengine.features.playrooms.domain.PlayRoomsData
import com.kglabs28.yapnplay.appengine.features.playrooms.repository.PlayRoomsRepository

/**
 * Loads the Rooms tab in one call: the user's rooms, friends who are
 * online now, and group invites waiting for them. Both halves are best-effort — a failure yields an empty
 * list and the screen shows its empty text, never a broken tab.
 */
class GetPlayRoomsScreenDataUseCase(
    private val roomsRepository: PlayRoomsRepository,
    private val profileRepository: ProfileRepository,
) {

    suspend operator fun invoke(): PlayRoomsData {
        val rooms = when (val result = roomsRepository.getMyRooms()) {
            is AppResult.Failure -> emptyList()
            is AppResult.Success -> result.data
        }
        val onlineFriends = when (val result = profileRepository.getFriendList()) {
            is AppResult.Failure -> emptyList()
            is AppResult.Success -> result.data.filter { it.online }
        }
        val invites = (roomsRepository.getInvites() as? AppResult.Success)?.data.orEmpty()
        return PlayRoomsData(rooms = rooms, onlineFriends = onlineFriends, invites = invites)
    }
}
