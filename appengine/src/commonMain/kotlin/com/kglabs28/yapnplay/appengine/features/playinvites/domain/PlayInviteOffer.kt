package com.kglabs28.yapnplay.appengine.features.playinvites.domain

/**
 * Someone invited the user to play now, in their Quick Play party: an
 * online friend (through the server) or a phone right next to them (Nearby).
 */
sealed interface PlayInviteOffer {
    val roomName: String
    val fromName: String
    val fromAvatarId: String

    data class Online(
        val inviteId: String,
        val roomId: String,
        override val roomName: String,
        override val fromName: String,
        override val fromAvatarId: String,
    ) : PlayInviteOffer

    data class Nearby(
        override val roomName: String,
        override val fromName: String,
        override val fromAvatarId: String,
    ) : PlayInviteOffer
}
