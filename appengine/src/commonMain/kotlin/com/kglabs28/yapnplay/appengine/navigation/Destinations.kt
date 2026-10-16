package com.kglabs28.yapnplay.appengine.navigation

import kotlinx.serialization.Serializable

/**
 * Type-safe destinations. Home and About exist now;
 * Room and Match are placeholders for later phases.
 */
@Serializable
data object Home

@Serializable
data object About

@Serializable
data object RoomPlaceholder

@Serializable
data object MatchPlaceholder
