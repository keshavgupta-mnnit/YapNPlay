package com.kglabs28.yapnplay.appengine.features.nearby.domain

import kotlinx.serialization.Serializable

/** Someone at a Nearby party: their account id, name and preset avatar. */
@Serializable
data class NearbyPlayer(
    val userId: String,
    val name: String,
    val avatarId: String = "",
)
