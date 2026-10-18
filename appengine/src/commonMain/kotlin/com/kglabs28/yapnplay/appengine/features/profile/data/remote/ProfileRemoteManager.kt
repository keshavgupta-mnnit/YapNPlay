package com.kglabs28.yapnplay.appengine.features.profile.data.remote

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.core.common.CrashReporter
import com.kglabs28.yapnplay.appengine.core.common.YapNPlayError
import com.kglabs28.yapnplay.appengine.features.profile.data.toDomain
import com.kglabs28.yapnplay.appengine.features.profile.domain.Friend
import com.kglabs28.yapnplay.appengine.features.profile.domain.FriendRequest
import com.kglabs28.yapnplay.appengine.features.profile.domain.FriendRequestOutcome
import com.kglabs28.yapnplay.appengine.features.profile.domain.UserProfile
import com.kglabs28.yapnplay.appengine.protocol.BlockUserBody
import com.kglabs28.yapnplay.appengine.protocol.SendFriendRequestBody
import com.kglabs28.yapnplay.appengine.protocol.UpdateProfileBody

/**
 * Network side of profile data: calls the API, maps DTOs to domain,
 * converts any failure into a typed error. Never throws.
 * One step above the API — Ktor handles success/failure internally.
 */
class ProfileRemoteManager(private val api: ProfileApi) {

    suspend fun fetchProfileData(): AppResult<UserProfile> = call("fetchProfileData") { api.getMe().toDomain() }

    suspend fun updateProfile(name: String?, username: String?, avatarId: String?): AppResult<UserProfile> =
        call("updateProfile") { api.updateMe(UpdateProfileBody(name, username, avatarId)).toDomain() }

    suspend fun fetchFriendList(): AppResult<List<Friend>> = call("fetchFriendList") { api.getFriends().friends.map { it.toDomain() } }

    suspend fun fetchFriendRequests(): AppResult<List<FriendRequest>> =
        call("fetchFriendRequests") { api.getFriendRequests().requests.map { it.toDomain() } }

    suspend fun sendFriendRequest(userCode: String): AppResult<FriendRequestOutcome> =
        call("sendFriendRequest") { api.sendFriendRequest(SendFriendRequestBody(userCode)).outcome.toDomain() }

    suspend fun respondToFriendRequest(requestId: String, accept: Boolean): AppResult<Unit> = call("respondToFriendRequest") {
        if (accept) api.acceptFriendRequest(requestId) else api.declineFriendRequest(requestId)
    }

    suspend fun removeFriend(userId: String): AppResult<Unit> = call("removeFriend") { api.removeFriend(userId) }

    suspend fun blockUser(userId: String): AppResult<Unit> = call("blockUser") { api.blockUser(BlockUserBody(userId)) }

    private suspend fun <T> call(what: String, block: suspend () -> T): AppResult<T> = try {
        AppResult.Success(block())
    } catch (e: Exception) {
        CrashReporter.record(e, "ProfileRemoteManager.$what")
        AppResult.Failure(YapNPlayError.Network("$what failed"))
    }
}
