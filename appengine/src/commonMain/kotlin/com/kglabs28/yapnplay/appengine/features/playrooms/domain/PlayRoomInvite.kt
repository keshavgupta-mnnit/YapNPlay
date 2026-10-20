package com.kglabs28.yapnplay.appengine.features.playrooms.domain

import com.kglabs28.yapnplay.appengine.features.profile.domain.Friend

/** A friend inviting the user into one of their groups; accepting makes the user a member at once. */
data class PlayRoomInvite(
    val inviteId: String,
    val roomId: String,
    val roomName: String,
    val icon: PlayRoomIcon,
    val memberCount: Int,
    val invitedBy: Friend,
)
