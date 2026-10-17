package com.kglabs28.yapnplay.appengine.features.playrooms.domain

/** A room seen before joining it (invite link or typed code). */
data class PlayRoomPreview(
    val roomId: String,
    val code: String,
    val name: String,
    val description: String,
    val memberCount: Int,
    val onlineCount: Int,
    val isMember: Boolean,
)
