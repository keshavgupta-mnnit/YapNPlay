package com.kglabs28.yapnplay.appengine.navigation

import kotlinx.serialization.Serializable

/**
 * Type-safe destinations. Main hosts the Home, Games and Rooms tabs;
 * Room creation is still a placeholder.
 */
@Serializable
data object Main

@Serializable
data object About

/** A play room's lobby (members, game choice, house rules, start). */
@Serializable
data class RoomLobby(val roomId: String)

@Serializable
data object RoomPlaceholder

/** A Royal Ludo table. */
@Serializable
data class RoyalLudoMatch(val matchId: String)

/** A City Baazar table. */
@Serializable
data class CityBaazarMatch(val matchId: String)
