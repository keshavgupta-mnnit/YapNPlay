package com.kglabs28.yapnplay.appengine.features.profile.data.remote

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.core.common.CrashReporter
import com.kglabs28.yapnplay.appengine.core.common.YapNPlayError
import com.kglabs28.yapnplay.appengine.features.profile.data.remote.ProfileApi
import com.kglabs28.yapnplay.appengine.features.profile.data.toDomain
import com.kglabs28.yapnplay.appengine.features.profile.domain.Friend
import com.kglabs28.yapnplay.appengine.features.profile.domain.UserProfile

/**
 * Network side of profile data: calls the API, maps DTOs to domain,
 * converts any failure into a typed error. Never throws.
 * One step above the API — Ktor handles success/failure internally.
 */
class ProfileRemoteManager(private val api: ProfileApi) {

    suspend fun fetchProfileData(): AppResult<UserProfile> = try {
        AppResult.Success(api.getProfile().toDomain())
    } catch (e: Exception) {
        CrashReporter.record(e, "ProfileRemoteManager.fetchProfileData")
        AppResult.Failure(YapNPlayError.Network("profile fetch failed"))
    }

    suspend fun fetchFriendList(): AppResult<List<Friend>> = try {
        AppResult.Success(api.getFriends().map { it.toDomain() })
    } catch (e: Exception) {
        CrashReporter.record(e, "ProfileRemoteManager.fetchFriendList")
        AppResult.Failure(YapNPlayError.Network("friends fetch failed"))
    }
}
