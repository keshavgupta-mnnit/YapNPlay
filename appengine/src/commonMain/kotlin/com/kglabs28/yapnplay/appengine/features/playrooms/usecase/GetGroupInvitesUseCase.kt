package com.kglabs28.yapnplay.appengine.features.playrooms.usecase

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.features.playrooms.domain.PlayRoomInvite
import com.kglabs28.yapnplay.appengine.features.playrooms.repository.PlayRoomsRepository

/** Group invites waiting for the user. */
class GetGroupInvitesUseCase(private val repository: PlayRoomsRepository) {
    suspend operator fun invoke(): AppResult<List<PlayRoomInvite>> = repository.getInvites()
}
