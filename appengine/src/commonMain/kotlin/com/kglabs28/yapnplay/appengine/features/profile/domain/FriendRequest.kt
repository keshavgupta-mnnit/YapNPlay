package com.kglabs28.yapnplay.appengine.features.profile.domain

/** Someone asking the user to be friends. */
data class FriendRequest(
    val requestId: String,
    val from: Friend,
)
