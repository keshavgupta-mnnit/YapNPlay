package com.kglabs28.yapnplay.appengine.ui.components.table

/** What every game's player card shows, whatever the game (each game's card model implements it). */
interface TablePlayerCardUi {
    val seat: Int
    val name: String
    /** The player's preset avatar ("" = person icon). */
    val avatarId: String
    /** Level number on the avatar's badge ("" for a CPU). */
    val level: String
    val isMe: Boolean
    val isTurn: Boolean
    val talking: Boolean
    /** 1 → 0 while it is this player's turn. */
    val timerFraction: Float
    /** The user muted this player for themselves only. */
    val mutedForMe: Boolean
    /** False for a CPU: no voice wave, no mute button, no friend long-press. */
    val canSpeak: Boolean
}
