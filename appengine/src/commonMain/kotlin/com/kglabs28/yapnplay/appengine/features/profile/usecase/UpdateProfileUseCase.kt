package com.kglabs28.yapnplay.appengine.features.profile.usecase

import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.core.common.YapNPlayError
import com.kglabs28.yapnplay.appengine.features.profile.domain.UserProfile
import com.kglabs28.yapnplay.appengine.features.profile.repository.ProfileRepository

/**
 * Changes the user's name, username or avatar (null = keep). Checks
 * first: a name of 1–[AppConstants.MAX_NAME_LENGTH] characters; a username
 * of [AppConstants.MIN_USERNAME_LENGTH]–[AppConstants.MAX_USERNAME_LENGTH]
 * letters, digits, "_" or "." (neither has to be unique).
 */
class UpdateProfileUseCase(private val repository: ProfileRepository) {

    suspend operator fun invoke(name: String? = null, username: String? = null, avatarId: String? = null): AppResult<UserProfile> {
        val cleanName = name?.trim()
        val cleanUsername = username?.trim()
        if (cleanName != null && (cleanName.isEmpty() || cleanName.length > AppConstants.MAX_NAME_LENGTH)) {
            return AppResult.Failure(YapNPlayError.Validation(FIELD_NAME))
        }
        if (cleanUsername != null && !validUsername(cleanUsername)) {
            return AppResult.Failure(YapNPlayError.Validation(FIELD_USERNAME))
        }
        return repository.updateProfile(cleanName, cleanUsername, avatarId)
    }

    private fun validUsername(value: String): Boolean =
        value.length in AppConstants.MIN_USERNAME_LENGTH..AppConstants.MAX_USERNAME_LENGTH &&
            value.all { it in AppConstants.USERNAME_CHARACTERS }

    companion object {
        const val FIELD_NAME = "name"
        const val FIELD_USERNAME = "username"
    }
}
