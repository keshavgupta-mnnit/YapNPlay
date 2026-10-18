package com.kglabs28.yapnplay.appengine.features.profile.domain

/** Someone the user blocked, and when ([blockedAt], epoch millis). */
data class BlockedUser(
    val userId: String,
    val name: String,
    val username: String,
    val avatarId: String,
    val blockedAt: Long,
)
