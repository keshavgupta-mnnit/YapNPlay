package com.kglabs28.yapnplay.appengine.features.playroomlobby.domain

/** One member's record in a room: matches finished there, won (1st), and coins earned. */
data class RoomMemberRecord(
    val userId: String,
    val name: String,
    val avatarId: String,
    val played: Int,
    val wins: Int,
    val coins: Int,
)
