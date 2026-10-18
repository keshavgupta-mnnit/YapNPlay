package com.kglabs28.yapnplay.appengine.features.profile.usecase

import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.core.common.YapNPlayError
import com.kglabs28.yapnplay.appengine.features.profile.domain.FriendRequestOutcome
import com.kglabs28.yapnplay.appengine.features.profile.repository.ProfileRepository

/** Asks the person with this 6-character ID to be friends (checks the ID's shape first). */
class SendFriendRequestUseCase(private val repository: ProfileRepository) {

    suspend operator fun invoke(userCode: String): AppResult<FriendRequestOutcome> {
        val code = userCode.trim().uppercase()
        if (code.length != AppConstants.USER_CODE_LENGTH || code.any { it !in AppConstants.USER_CODE_ALPHABET }) {
            return AppResult.Failure(YapNPlayError.Validation(FIELD_CODE))
        }
        return repository.sendFriendRequest(code)
    }

    companion object {
        const val FIELD_CODE = "userCode"
    }
}
