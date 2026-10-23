package com.kglabs28.yapnplay.appengine.features.profile.domain

/**
 * A friend: name, their 6-character ID, preset
 * avatar and whether they're online right now.
 */
data class Friend(
    val userId: String,
    val name: String,
    val online: Boolean,
    val userCode: String = "",
    val avatarId: String = "",
)
