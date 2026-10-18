package com.kglabs28.yapnplay.appengine.features.profile.usecase

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.features.profile.repository.ProfileRepository

/** Blocks a person: ends any friendship and request; they can't send requests or invites. */
class BlockUserUseCase(private val repository: ProfileRepository) {
    suspend operator fun invoke(userId: String): AppResult<Unit> = repository.blockUser(userId)
}
