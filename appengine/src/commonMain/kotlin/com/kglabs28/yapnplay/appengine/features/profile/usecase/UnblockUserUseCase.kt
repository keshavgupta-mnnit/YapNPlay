package com.kglabs28.yapnplay.appengine.features.profile.usecase

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.features.profile.repository.ProfileRepository

/** Lifts a block (they can send friend requests and invites again; no friendship comes back). */
class UnblockUserUseCase(private val repository: ProfileRepository) {
    suspend operator fun invoke(userId: String): AppResult<Unit> = repository.unblockUser(userId)
}
