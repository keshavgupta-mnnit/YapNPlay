package com.kglabs28.yapnplay.appengine.features.profile.data

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.features.profile.data.local.ProfileLocalManager
import com.kglabs28.yapnplay.appengine.features.profile.data.remote.ProfileRemoteManager
import com.kglabs28.yapnplay.appengine.features.profile.domain.Friend
import com.kglabs28.yapnplay.appengine.features.profile.domain.UserProfile
import com.kglabs28.yapnplay.appengine.features.profile.repository.ProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Network first, cache fallback: fresh data wins, offline still works.
 * Callers see only [ProfileRepository]; remote, cache and mapping
 * details stay behind this wall. Work runs on IO.
 */
class ProfileRepositoryImpl(
    private val remote: ProfileRemoteManager,
    private val local: ProfileLocalManager,
) : ProfileRepository {

    override suspend fun fetchProfileData(): AppResult<UserProfile> =
        withContext(Dispatchers.IO) {
            when (val result = remote.fetchProfileData()) {
                is AppResult.Success -> {
                    local.saveProfile(result.data)
                    result
                }
                is AppResult.Failure -> {
                    local.profile()?.let { AppResult.Success(it) } ?: result
                }
            }
        }

    override suspend fun getFriendList(): AppResult<List<Friend>> =
        withContext(Dispatchers.IO) {
            when (val result = remote.fetchFriendList()) {
                is AppResult.Success -> {
                    local.saveFriends(result.data)
                    result
                }
                is AppResult.Failure -> {
                    val cached = local.friends()
                    if (cached.isNotEmpty()) AppResult.Success(cached) else result
                }
            }
        }
}
