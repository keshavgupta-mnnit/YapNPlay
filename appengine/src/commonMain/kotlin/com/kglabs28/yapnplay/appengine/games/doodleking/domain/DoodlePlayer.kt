package com.kglabs28.yapnplay.appengine.games.doodleking.domain

data class DoodlePlayer(
    val seat: Int,
    val userId: String,
    val name: String,
    val level: Int,
    val avatarId: String,
    val isMe: Boolean,
    val micOn: Boolean,
    val isCpu: Boolean,
    val away: Boolean,
    /** The bot holds the seat for someone who left it (the leader may offer it to a spectator). */
    val botHeld: Boolean,
    val removed: Boolean,
    val score: Int,
    val guessed: Boolean,
    val turnPoints: Int,
)
