package com.kglabs28.yapnplay.appengine.features.playrooms.usecase

import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.core.common.YapNPlayError
import com.kglabs28.yapnplay.appengine.features.playrooms.domain.CreatedPlayRoom
import com.kglabs28.yapnplay.appengine.features.playrooms.domain.PlayRoomIcon
import com.kglabs28.yapnplay.appengine.features.playrooms.repository.PlayRoomsRepository

/**
 * Creates a group: the user becomes its member, leader and admin, and the
 * chosen friends get invites. It needs a name and at least one friend; at
 * most [AppConstants.MAX_MEMBERS] − 1 friends (the user is the first member).
 * The server refuses a second group with exactly the same members.
 */
class CreatePlayRoomUseCase(private val repository: PlayRoomsRepository) {
    suspend operator fun invoke(
        name: String? = null,
        icon: PlayRoomIcon = PlayRoomIcon.GROUP,
        inviteUserIds: List<String> = emptyList(),
    ): AppResult<CreatedPlayRoom> {
        val clean = name?.trim().orEmpty()
        if (clean.isEmpty()) return AppResult.Failure(YapNPlayError.Validation(FIELD_NAME_EMPTY))
        if (clean.length > AppConstants.MAX_ROOM_NAME_LENGTH) return AppResult.Failure(YapNPlayError.Validation(FIELD_NAME))
        if (inviteUserIds.isEmpty()) return AppResult.Failure(YapNPlayError.Validation(FIELD_MEMBERS))
        if (inviteUserIds.size > AppConstants.MAX_MEMBERS - 1) return AppResult.Failure(YapNPlayError.LimitReached(AppConstants.MAX_MEMBERS))
        return repository.createRoom(clean, icon.key, inviteUserIds.distinct())
    }

    companion object {
        const val FIELD_NAME = "groupName"
        const val FIELD_NAME_EMPTY = "groupNameEmpty"
        const val FIELD_MEMBERS = "groupMembers"
    }
}
