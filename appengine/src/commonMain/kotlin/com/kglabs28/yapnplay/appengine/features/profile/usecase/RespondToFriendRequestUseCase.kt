package com.kglabs28.yapnplay.appengine.features.profile.usecase

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.features.profile.repository.ProfileRepository

/** Accepts or declines someone's friend request. */
class RespondToFriendRequestUseCase(private val repository: ProfileRepository) {
    suspend operator fun invoke(requestId: String, accept: Boolean): AppResult<Unit> = repository.respondToFriendRequest(requestId, accept)
}
