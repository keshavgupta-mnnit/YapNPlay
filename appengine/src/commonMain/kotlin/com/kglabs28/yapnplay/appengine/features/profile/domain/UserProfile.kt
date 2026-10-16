package com.kglabs28.yapnplay.appengine.features.profile.domain

/**
 * The signed-in user, from the repository (fake data today, backend later).
 * Raw values only — formatting for display happens at the screen edge.
 */
data class UserProfile(
    val name: String,
    val level: Int,
    val xp: Int,
    val xpMax: Int,
    val online: Boolean,
    val coins: Int,
    val gems: Int,
)
