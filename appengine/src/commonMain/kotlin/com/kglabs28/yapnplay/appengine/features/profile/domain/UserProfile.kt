package com.kglabs28.yapnplay.appengine.features.profile.domain

/**
 * The signed-in user, from the repository (fake data today, backend later).
 * Raw values only — formatting for display happens at the screen edge.
 * [xp] is computed by the server from all match results; [level] derives
 * from it. [coins] and [gems] are provisional (economy not designed yet).
 * [username] needn't be unique; [userCode] (6 characters) is the unique ID
 * friends type to add them. [avatarId] is one of the preset avatars.
 */
data class UserProfile(
    val userId: String,
    val name: String,
    val xp: Int,
    val level: Int,
    val coins: Int,
    val gems: Int,
    val username: String = "",
    val userCode: String = "",
    val avatarId: String = "",
    val gamesPlayed: Int = 0,
    val gamesWon: Int = 0,
)
