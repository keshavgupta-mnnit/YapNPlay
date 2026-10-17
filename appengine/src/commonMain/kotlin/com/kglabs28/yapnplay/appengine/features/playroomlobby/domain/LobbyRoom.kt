package com.kglabs28.yapnplay.appengine.features.playroomlobby.domain

import kotlinx.datetime.Instant

/**
 * The live room behind the lobby screen, rebuilt from every server
 * snapshot. [selectedGameId] null means the leader is still choosing.
 * [countdownEndsAt] is already on this device's clock.
 */
data class LobbyRoom(
    val id: String,
    val name: String,
    val description: String,
    val code: String,
    val members: List<LobbyMember>,
    val selectedGameId: String?,
    val houseRules: Map<String, Boolean>,
    val botFill: Boolean,
    val countdownEndsAt: Instant?,
)
