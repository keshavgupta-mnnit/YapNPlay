package com.kglabs28.yapnplay.appengine.features.profile.usecase

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.features.profile.repository.ProfileRepository

/** Ends a friendship (either side can add the other again later). */
class RemoveFriendUseCase(private val repository: ProfileRepository) {
    suspend operator fun invoke(userId: String): AppResult<Unit> = repository.removeFriend(userId)
}
