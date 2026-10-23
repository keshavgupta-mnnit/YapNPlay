package com.kglabs28.yapnplay.appengine.features.profile.usecase

import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.core.common.YapNPlayError
import com.kglabs28.yapnplay.appengine.features.profile.domain.UserProfile
import com.kglabs28.yapnplay.appengine.features.profile.repository.ProfileRepository

/**
 * Changes the user's name or avatar (null = keep). Checks first: a name of
 * 1-[AppConstants.MAX_NAME_LENGTH] characters (it needn't be unique).
 */
class UpdateProfileUseCase(private val repository: ProfileRepository) {

    suspend operator fun invoke(name: String? = null, avatarId: String? = null): AppResult<UserProfile> {
        val cleanName = name?.trim()
        if (cleanName != null && (cleanName.isEmpty() || cleanName.length > AppConstants.MAX_NAME_LENGTH)) {
            return AppResult.Failure(YapNPlayError.Validation(FIELD_NAME))
        }
        return repository.updateProfile(cleanName, avatarId)
    }

    companion object {
        const val FIELD_NAME = "name"
    }
}
