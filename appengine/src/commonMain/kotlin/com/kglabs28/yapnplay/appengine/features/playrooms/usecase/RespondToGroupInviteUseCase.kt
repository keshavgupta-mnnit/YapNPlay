package com.kglabs28.yapnplay.appengine.features.playrooms.usecase

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.features.playrooms.repository.PlayRoomsRepository

/** Accepts (joins at once) or declines a group invite. */
class RespondToGroupInviteUseCase(private val repository: PlayRoomsRepository) {
    suspend operator fun invoke(inviteId: String, accept: Boolean): AppResult<Unit> =
        if (accept) {
            when (val result = repository.acceptInvite(inviteId)) {
                is AppResult.Success -> AppResult.Success(Unit)
                is AppResult.Failure -> result
            }
        } else {
            repository.declineInvite(inviteId)
        }
}
