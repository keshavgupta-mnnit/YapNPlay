package com.kglabs28.yapnplay.appengine.features.profile.data.remote

import com.kglabs28.yapnplay.appengine.features.profile.data.remote.dto.FriendDto
import com.kglabs28.yapnplay.appengine.features.profile.data.remote.dto.ProfileDto

/**
 * Profile endpoints. Shapes match docs/backend-api-contract.md.
 */
interface ProfileApi {
    suspend fun getProfile(): ProfileDto
    suspend fun getFriends(): List<FriendDto>
}
