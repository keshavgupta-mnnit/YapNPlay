package com.kglabs28.yapnplay.appengine.features.playrooms.domain

/**
 * A room the user has joined or created. [liveCount] is how many members
 * are in the room right now — the list shows that, not chat activity.
 */
data class PlayRoom(
    val id: String,
    val name: String,
    val code: String,
    val memberCount: Int,
    val liveCount: Int,
    val isOwner: Boolean,
    val icon: PlayRoomIcon,
)
