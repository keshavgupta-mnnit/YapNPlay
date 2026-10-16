package com.kglabs28.yapnplay.appengine.features.profile.data

import com.kglabs28.yapnplay.appengine.features.profile.data.remote.dto.FriendDto
import com.kglabs28.yapnplay.appengine.features.profile.data.remote.dto.ProfileDto
import com.kglabs28.yapnplay.appengine.features.profile.domain.Friend
import com.kglabs28.yapnplay.appengine.features.profile.domain.UserProfile

/** Wire DTO to domain. Single place: backend shape changes land here. */
fun ProfileDto.toDomain(): UserProfile = UserProfile(
    name = displayName,
    level = level,
    xp = xp,
    xpMax = xpMax,
    online = online,
    coins = coins,
    gems = gems,
)

fun FriendDto.toDomain(): Friend = Friend(
    userId = userId,
    name = displayName,
    online = online,
)
