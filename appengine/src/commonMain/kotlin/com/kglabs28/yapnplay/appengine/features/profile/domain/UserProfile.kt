package com.kglabs28.yapnplay.appengine.features.profile.domain

/**
 * The signed-in user, from the repository (fake data today, backend later).
 * Raw values only — formatting for display happens at the screen edge.
 * [xp] is computed by the server from all match results; [level] derives
 * from it. [coins] and [gems] are provisional (economy not designed yet).
 */
data class UserProfile(
    val userId: String,
    val name: String,
    val xp: Int,
    val level: Int,
    val coins: Int,
    val gems: Int,
)
