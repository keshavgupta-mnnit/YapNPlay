package com.kglabs28.yapnplay.appengine.features.profile.usecase

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.features.profile.domain.Friend
import com.kglabs28.yapnplay.appengine.features.profile.repository.ProfileRepository

/** All the user's friends (online and offline). */
class GetFriendsUseCase(private val repository: ProfileRepository) {
    suspend operator fun invoke(): AppResult<List<Friend>> = repository.getFriendList()
}
