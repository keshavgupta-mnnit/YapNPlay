package com.kglabs28.yapnplay.appengine.features.profile.domain

/** Everything the Profile screen shows, loaded in one go. */
data class ProfileScreenData(
    val profile: UserProfile,
    val friends: List<Friend>,
    val requests: List<FriendRequest>,
)
