package com.kglabs28.yapnplay.appengine.navigation

import kotlinx.serialization.Serializable

/**
 * Type-safe destinations. Main hosts the Home, Games and Rooms tabs;
 * Room and Match are placeholders for later phases.
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

@Serializable
data object MatchPlaceholder
