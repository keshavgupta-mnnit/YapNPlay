package com.kglabs28.yapnplay.appengine.features.playrooms.usecase

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.features.playrooms.domain.PlayRoom
import com.kglabs28.yapnplay.appengine.features.playrooms.repository.PlayRoomsRepository

/** The groups the user belongs to (for "Invite to Group"). */
class GetMyGroupsUseCase(private val repository: PlayRoomsRepository) {
    suspend operator fun invoke(): AppResult<List<PlayRoom>> = repository.getMyRooms()
}
