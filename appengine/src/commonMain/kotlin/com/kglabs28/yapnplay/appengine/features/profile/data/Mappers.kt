package com.kglabs28.yapnplay.appengine.features.profile.data

import com.kglabs28.yapnplay.appengine.features.profile.domain.Friend
import com.kglabs28.yapnplay.appengine.features.profile.domain.UserProfile
import com.kglabs28.yapnplay.appengine.protocol.FriendSummary
import com.kglabs28.yapnplay.appengine.protocol.Me

/** Contract types to domain. Single place: contract changes land here. */
fun Me.toDomain(): UserProfile = UserProfile(
    userId = userId,
    name = displayName,
    xp = xp,
    level = level,
    coins = coins,
    gems = gems,
)

fun FriendSummary.toDomain(): Friend = Friend(
    userId = userId,
    name = displayName,
    online = online,
)
