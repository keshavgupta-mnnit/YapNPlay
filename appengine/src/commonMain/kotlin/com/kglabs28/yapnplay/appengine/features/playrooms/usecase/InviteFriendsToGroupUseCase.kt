package com.kglabs28.yapnplay.appengine.features.playrooms.usecase

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.features.playrooms.repository.PlayRoomsRepository

/** Invites friends (and players by [userCodes]) into one of the user's groups; returns the ids that got an invite. */
class InviteFriendsToGroupUseCase(private val repository: PlayRoomsRepository) {
    suspend operator fun invoke(roomId: String, userIds: List<String>, userCodes: List<String> = emptyList()): AppResult<List<String>> =
        repository.inviteToRoom(roomId, userIds, userCodes)
}
