package com.kglabs28.yapnplay.appengine.features.profile.usecase

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.features.profile.domain.ProfileScreenData
import com.kglabs28.yapnplay.appengine.features.profile.repository.ProfileRepository

/**
 * Loads the Profile screen in one call. The profile is essential (its
 * failure fails the load); friends and requests are best-effort (empty
 * lists, never a broken screen).
 */
class GetProfileScreenDataUseCase(private val repository: ProfileRepository) {

    suspend operator fun invoke(): AppResult<ProfileScreenData> {
        val profile = when (val result = repository.fetchProfileData()) {
            is AppResult.Failure -> return result
            is AppResult.Success -> result.data
        }
        val friends = (repository.getFriendList() as? AppResult.Success)?.data.orEmpty()
        val requests = (repository.getFriendRequests() as? AppResult.Success)?.data.orEmpty()
        return AppResult.Success(ProfileScreenData(profile, friends, requests))
    }
}
