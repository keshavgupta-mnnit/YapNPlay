package com.kglabs28.yapnplay.appengine.features.playrooms.usecase

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.features.playrooms.repository.PlayRoomsRepository

/**
 * Creates a play room; the user becomes its member, leader and admin.
 * Without a name the server picks "<name>'s Room". Returns the room id.
 */
class CreatePlayRoomUseCase(private val repository: PlayRoomsRepository) {
    suspend operator fun invoke(name: String? = null): AppResult<String> = repository.createRoom(name)
}
