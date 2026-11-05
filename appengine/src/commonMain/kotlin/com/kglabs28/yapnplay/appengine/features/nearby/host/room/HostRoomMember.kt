package com.kglabs28.yapnplay.appengine.features.nearby.host.room

/** One person in the party room the phone hosts, as the backend's room keeps a live member. */
data class HostRoomMember(
    val userId: String,
    val displayName: String,
    val avatarId: String,
    val joinedAt: Long,
    /** In the lobby right now (their table counts too). */
    val online: Boolean = false,
    /** When they came in this time; leader succession uses it. */
    val onlineSince: Long? = null,
    val ready: Boolean = false,
    /** When they pressed ready; seats go to the earliest. */
    val readyAt: Long? = null,
    val watching: Boolean = false,
    val micOn: Boolean = false,
    /** Off to start with: a Nearby party begins quiet. */
    val speakerOn: Boolean = false,
    val talkingMuted: Boolean = false,
    /** How well their phone could host (HostFitness): the party hands over to the best. */
    val fitness: Int = 0,
)
