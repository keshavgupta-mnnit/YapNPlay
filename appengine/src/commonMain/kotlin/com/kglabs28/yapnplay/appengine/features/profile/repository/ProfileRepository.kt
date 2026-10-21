package com.kglabs28.yapnplay.appengine.features.profile.repository

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.features.profile.domain.BlockedUser
import com.kglabs28.yapnplay.appengine.features.profile.domain.Friend
import com.kglabs28.yapnplay.appengine.features.profile.domain.FriendRequest
import com.kglabs28.yapnplay.appengine.features.profile.domain.FriendRequestOutcome
import com.kglabs28.yapnplay.appengine.features.profile.domain.UserProfile
import kotlinx.coroutines.flow.StateFlow

/**
 * Who the user is, who their friends are, and the friend actions.
 * Implemented by [ProfileRepositoryImpl] over remote + local managers; a
 * backend implementation replaces the API client without touching callers.
 */
interface ProfileRepository {
    /** The user as last loaded or edited (null before the first load). */
    val profile: StateFlow<UserProfile?>

    /**
     * Today's daily bonus once the server granted it (on whichever /me call
     * came first), until the user collects it on Home; 0 otherwise.
     */
    val pendingDailyBonus: StateFlow<Int>
    fun collectDailyBonus()

    suspend fun fetchProfileData(): AppResult<UserProfile>
    suspend fun updateProfile(name: String? = null, username: String? = null, avatarId: String? = null): AppResult<UserProfile>
    suspend fun getFriendList(): AppResult<List<Friend>>
    suspend fun getFriendRequests(): AppResult<List<FriendRequest>>
    suspend fun sendFriendRequest(userCode: String): AppResult<FriendRequestOutcome>
    /** Same, by user id (a player card in a game). */
    suspend fun sendFriendRequestToUser(userId: String): AppResult<FriendRequestOutcome>
    suspend fun respondToFriendRequest(requestId: String, accept: Boolean): AppResult<Unit>
    suspend fun removeFriend(userId: String): AppResult<Unit>
    suspend fun blockUser(userId: String): AppResult<Unit>
    suspend fun getBlockedUsers(): AppResult<List<BlockedUser>>
    suspend fun unblockUser(userId: String): AppResult<Unit>
}
