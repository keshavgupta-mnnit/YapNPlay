package com.kglabs28.yapnplay.appengine.features.profile.usecase

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.features.profile.repository.ProfileRepository

/** The user ids of the user's friends (empty if they can't be loaded). */
class GetFriendIdsUseCase(private val repository: ProfileRepository) {
    suspend operator fun invoke(): Set<String> =
        (repository.getFriendList() as? AppResult.Success)?.data.orEmpty().mapTo(mutableSetOf()) { it.userId }
}
