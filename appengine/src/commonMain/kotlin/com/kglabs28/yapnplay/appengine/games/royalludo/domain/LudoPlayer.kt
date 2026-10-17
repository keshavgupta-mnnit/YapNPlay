package com.kglabs28.yapnplay.appengine.games.royalludo.domain

/** One seat at the table. */
data class LudoPlayer(
    val seat: Int,
    val userId: String,
    val name: String,
    val level: Int,
    val colour: LudoColour,
    val isMe: Boolean,
    val micOn: Boolean,
    /** The server plays this seat (CPU, away, or the owner turned autoplay on). */
    val autoplay: Boolean,
    val removed: Boolean,
)
