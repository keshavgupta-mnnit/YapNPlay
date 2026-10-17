package com.kglabs28.yapnplay.appengine.features.playroomlobby.domain

/**
 * One member as the lobby shows them. Roles come from the room
 * ([isLeader], [isAdmin]); [isFriend] is relative to the signed-in user.
 */
data class LobbyMember(
    val userId: String,
    val name: String,
    val online: Boolean,
    val ready: Boolean,
    val watching: Boolean,
    val micOn: Boolean,
    val speakerOn: Boolean,
    val isFriend: Boolean,
    val isLeader: Boolean,
    val isAdmin: Boolean,
)
