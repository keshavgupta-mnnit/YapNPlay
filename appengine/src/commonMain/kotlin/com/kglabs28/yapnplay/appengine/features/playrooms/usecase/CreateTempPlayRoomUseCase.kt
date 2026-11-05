package com.kglabs28.yapnplay.appengine.features.playrooms.usecase

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.features.playrooms.domain.CreatedPlayRoom
import com.kglabs28.yapnplay.appengine.features.playrooms.repository.PlayRoomsRepository

/**
 * Quick Play with friends: a temporary room the user leads. Anyone in it
 * can invite their friends; only the leader picks the game; the server
 * deletes it once everyone has left its lobby.
 */
class CreateTempPlayRoomUseCase(private val repository: PlayRoomsRepository) {
    suspend operator fun invoke(): AppResult<CreatedPlayRoom> = repository.createTempRoom()
}
