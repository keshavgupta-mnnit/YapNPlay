package com.kglabs28.yapnplay.appengine.features.profile.usecase

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.features.profile.domain.Friend
import com.kglabs28.yapnplay.appengine.features.profile.domain.HomeData
import com.kglabs28.yapnplay.appengine.features.profile.domain.UserProfile
import com.kglabs28.yapnplay.appengine.features.profile.repository.ProfileRepository

/**
 * Loads the complete home screen payload in one call. Profile is
 * essential (its failure fails the load); friends are best-effort
 * (their failure yields an empty list, never a broken screen).
 */
class GetHomeScreenData(private val repository: ProfileRepository) {

    suspend operator fun invoke(): AppResult<HomeData> {
        val profile = when (val result = repository.fetchProfileData()) {
            is AppResult.Failure -> return result
            is AppResult.Success -> result.data
        }
        val friends = when (val result = repository.getFriendList()) {
            is AppResult.Failure -> emptyList()
            is AppResult.Success -> result.data
        }
        return AppResult.Success(HomeData(profile = profile, friends = friends))
    }
}
