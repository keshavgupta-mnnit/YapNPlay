package com.kglabs28.yapnplay.appengine.features.seathandover.domain

/** Someone still in the room during a match (player or spectator), online since [onlineSince] (epoch millis). */
data class PresentMember(val userId: String, val name: String, val onlineSince: Long)
