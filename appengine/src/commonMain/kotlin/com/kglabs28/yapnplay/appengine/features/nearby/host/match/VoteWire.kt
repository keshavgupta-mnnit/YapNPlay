package com.kglabs28.yapnplay.appengine.features.nearby.host.match

import kotlinx.serialization.Serializable

/** The generic event sent when the end-game vote is decided (same JSON as the backend's). */
@Serializable
data class EndVotedWire(
    val ended: Boolean,
    val yes: Int,
    val voters: Int,
    val type: String = "EndVoted",
)

/** The generic event sent when the keep-playing vote is decided (same JSON as the backend's). */
@Serializable
data class ExtensionVotedWire(
    val continued: Boolean,
    val yes: Int,
    val voters: Int,
    val type: String = "ExtensionVoted",
)
