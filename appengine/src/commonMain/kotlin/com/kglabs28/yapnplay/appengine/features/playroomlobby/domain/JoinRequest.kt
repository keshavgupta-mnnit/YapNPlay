package com.kglabs28.yapnplay.appengine.features.playroomlobby.domain

/** Someone on the admin's Waiting List, asking to join the room. */
data class JoinRequest(
    val userId: String,
    val name: String,
    val requestedAt: Long,
)
