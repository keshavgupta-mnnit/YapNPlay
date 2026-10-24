package com.kglabs28.yapnplay.appengine.features.playroomlobby.usecase

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.features.playroomlobby.repository.RoomPendingInvitesRepository

/** Friends already invited to the room and not answered yet (they can't be picked again). */
class GetRoomPendingInvitesUseCase(private val repository: RoomPendingInvitesRepository) {
    suspend operator fun invoke(roomId: String): AppResult<Set<String>> = repository.pendingUserIds(roomId)
}
