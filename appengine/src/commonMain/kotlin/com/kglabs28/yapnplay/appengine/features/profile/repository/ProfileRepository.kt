package com.kglabs28.yapnplay.appengine.features.profile.repository

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.features.profile.domain.Friend
import com.kglabs28.yapnplay.appengine.features.profile.domain.UserProfile

/**
 * Who the user is and who their friends are. Implemented by
 * [ProfileRepositoryImpl] over remote + local managers; a backend
 * implementation replaces the API client without touching callers.
 */
interface ProfileRepository {
    suspend fun fetchProfileData(): AppResult<UserProfile>
    suspend fun getFriendList(): AppResult<List<Friend>>
}
