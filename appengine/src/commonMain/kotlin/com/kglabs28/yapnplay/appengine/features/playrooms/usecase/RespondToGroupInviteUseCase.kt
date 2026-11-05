package com.kglabs28.yapnplay.appengine.features.playrooms.usecase

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.features.playrooms.domain.PlayRoom
import com.kglabs28.yapnplay.appengine.features.playrooms.repository.PlayRoomsRepository

/** Accepts (joins at once: the room joined) or declines (null) a group invite. */
class RespondToGroupInviteUseCase(private val repository: PlayRoomsRepository) {
    suspend operator fun invoke(inviteId: String, accept: Boolean): AppResult<PlayRoom?> =
        if (accept) {
            repository.acceptInvite(inviteId)
        } else {
            when (val result = repository.declineInvite(inviteId)) {
                is AppResult.Success -> AppResult.Success(null)
                is AppResult.Failure -> result
            }
        }
}
