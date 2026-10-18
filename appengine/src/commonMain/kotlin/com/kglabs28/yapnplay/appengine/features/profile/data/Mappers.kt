package com.kglabs28.yapnplay.appengine.features.profile.data

import com.kglabs28.yapnplay.appengine.features.profile.domain.BlockedUser
import com.kglabs28.yapnplay.appengine.features.profile.domain.Friend
import com.kglabs28.yapnplay.appengine.features.profile.domain.FriendRequest
import com.kglabs28.yapnplay.appengine.features.profile.domain.FriendRequestOutcome
import com.kglabs28.yapnplay.appengine.features.profile.domain.UserProfile
import com.kglabs28.yapnplay.appengine.protocol.BlockedUserSummary
import com.kglabs28.yapnplay.appengine.protocol.FriendRequestOutcomeCode
import com.kglabs28.yapnplay.appengine.protocol.FriendRequestSummary
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
    username = username,
    userCode = userCode,
    avatarId = avatarId,
    gamesPlayed = gamesPlayed,
    gamesWon = gamesWon,
)

fun FriendSummary.toDomain(): Friend = Friend(
    userId = userId,
    name = displayName,
    online = online,
    username = username,
    userCode = userCode,
    avatarId = avatarId,
)

fun FriendRequestSummary.toDomain(): FriendRequest = FriendRequest(requestId, from.toDomain())

fun FriendRequestOutcomeCode.toDomain(): FriendRequestOutcome = when (this) {
    FriendRequestOutcomeCode.SENT -> FriendRequestOutcome.SENT
    FriendRequestOutcomeCode.NOT_FOUND -> FriendRequestOutcome.NOT_FOUND
    FriendRequestOutcomeCode.SELF -> FriendRequestOutcome.SELF
    FriendRequestOutcomeCode.ALREADY_FRIENDS -> FriendRequestOutcome.ALREADY_FRIENDS
    FriendRequestOutcomeCode.ALREADY_REQUESTED -> FriendRequestOutcome.ALREADY_REQUESTED
    FriendRequestOutcomeCode.BLOCKED -> FriendRequestOutcome.BLOCKED
    FriendRequestOutcomeCode.ACCEPTED -> FriendRequestOutcome.ACCEPTED
}

fun BlockedUserSummary.toDomain(): BlockedUser = BlockedUser(userId, displayName, username, avatarId, blockedAt)
