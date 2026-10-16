package com.kglabs28.yapnplay.appengine.features.profile.domain

/**
 * One friend on the home orbit. Avatar art arrives later;
 * the UI assigns colors by position meanwhile.
 */
data class Friend(
    val userId: String,
    val name: String,
    val online: Boolean,
)
