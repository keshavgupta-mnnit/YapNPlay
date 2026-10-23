package com.kglabs28.yapnplay.appengine.features.profile.data

import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.features.profile.data.local.ProfileLocalManager
import com.kglabs28.yapnplay.appengine.features.profile.data.remote.ProfileRemoteManager
import com.kglabs28.yapnplay.appengine.features.profile.domain.BlockedUser
import com.kglabs28.yapnplay.appengine.features.profile.domain.Friend
import com.kglabs28.yapnplay.appengine.features.profile.domain.FriendRequest
import com.kglabs28.yapnplay.appengine.features.profile.domain.FriendRequestOutcome
import com.kglabs28.yapnplay.appengine.features.profile.domain.UserProfile
import com.kglabs28.yapnplay.appengine.features.profile.repository.ProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
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

    override val profile: StateFlow<UserProfile?> get() = local.profileFlow

    private val dailyBonus = MutableStateFlow(0)
    override val pendingDailyBonus: StateFlow<Int> = dailyBonus.asStateFlow()

    override fun collectDailyBonus() {
        dailyBonus.value = 0
    }

    override suspend fun fetchProfileData(): AppResult<UserProfile> =
        withContext(Dispatchers.IO) {
            when (val result = remote.fetchProfileData()) {
                is AppResult.Success -> {
                    // The server grants the bonus once a day, on the first /me: keep it for Home.
                    if (result.data.dailyBonus > 0) dailyBonus.value = result.data.dailyBonus
                    local.saveProfile(result.data.copy(dailyBonus = 0))
                    result
                }
                is AppResult.Failure -> {
                    local.profile()?.let { AppResult.Success(it) } ?: result
                }
            }
        }

    override suspend fun updateProfile(name: String?, avatarId: String?): AppResult<UserProfile> =
        withContext(Dispatchers.IO) {
            remote.updateProfile(name, avatarId).also { if (it is AppResult.Success) local.saveProfile(it.data) }
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

    override suspend fun getFriendRequests(): AppResult<List<FriendRequest>> =
        withContext(Dispatchers.IO) { remote.fetchFriendRequests() }

    override suspend fun sendFriendRequest(userCode: String): AppResult<FriendRequestOutcome> =
        withContext(Dispatchers.IO) { remote.sendFriendRequest(userCode) }

    override suspend fun sendFriendRequestToUser(userId: String): AppResult<FriendRequestOutcome> =
        withContext(Dispatchers.IO) { remote.sendFriendRequestToUser(userId) }

    override suspend fun respondToFriendRequest(requestId: String, accept: Boolean): AppResult<Unit> =
        withContext(Dispatchers.IO) { remote.respondToFriendRequest(requestId, accept) }

    override suspend fun removeFriend(userId: String): AppResult<Unit> =
        withContext(Dispatchers.IO) {
            remote.removeFriend(userId).also { if (it is AppResult.Success) local.saveFriends(local.friends().filterNot { f -> f.userId == userId }) }
        }

    override suspend fun blockUser(userId: String): AppResult<Unit> =
        withContext(Dispatchers.IO) {
            remote.blockUser(userId).also { if (it is AppResult.Success) local.saveFriends(local.friends().filterNot { f -> f.userId == userId }) }
        }

    override suspend fun getBlockedUsers(): AppResult<List<BlockedUser>> =
        withContext(Dispatchers.IO) { remote.fetchBlockedUsers() }

    override suspend fun unblockUser(userId: String): AppResult<Unit> =
        withContext(Dispatchers.IO) { remote.unblockUser(userId) }
}
