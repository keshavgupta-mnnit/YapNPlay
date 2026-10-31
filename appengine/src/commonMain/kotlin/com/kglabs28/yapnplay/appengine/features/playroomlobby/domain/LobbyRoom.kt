package com.kglabs28.yapnplay.appengine.features.playroomlobby.domain

import com.kglabs28.yapnplay.appengine.features.playrooms.domain.PlayRoomIcon
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
    val icon: PlayRoomIcon,
    val code: String,
    val members: List<LobbyMember>,
    val selectedGameId: String?,
    val houseRules: Map<String, Boolean>,
    val botFill: Boolean,
    /** The room's league (each player's entry, coins), the leagues on offer, and the most the lobby can pay. */
    val entryCoins: Int = 0,
    val leagueFees: List<Int> = emptyList(),
    val affordableEntry: Int = Int.MAX_VALUE,
    val countdownEndsAt: Instant?,
    /** A match already running in this room (members can watch it), or null. */
    val matchInProgress: String? = null,
    /** People asking to join; the server fills it only for the admin. */
    val waitingList: List<JoinRequest> = emptyList(),
    /** Why the last start countdown stopped, and who stopped it; null since one started. */
    val countdownStop: CountdownStopInfo? = null,
)
