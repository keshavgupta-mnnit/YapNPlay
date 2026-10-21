package com.kglabs28.yapnplay.appengine.features.profile.usecase

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.features.profile.repository.ProfileRepository

/** From a player card in a game: removes a friend, or asks someone else to be friends. */
class TogglePlayerFriendUseCase(private val repository: ProfileRepository) {
    suspend operator fun invoke(userId: String, isFriend: Boolean): AppResult<Unit> =
        if (isFriend) {
            repository.removeFriend(userId)
        } else {
            when (val result = repository.sendFriendRequestToUser(userId)) {
                is AppResult.Success -> AppResult.Success(Unit)
                is AppResult.Failure -> result
            }
        }
}
