package com.kglabs28.yapnplay.appengine.ui.screens.playrooms

import com.kglabs28.yapnplay.appengine.core.mvi.UiEffect
import com.kglabs28.yapnplay.appengine.core.mvi.UiIntent
import com.kglabs28.yapnplay.appengine.core.mvi.UiState
import com.kglabs28.yapnplay.appengine.features.profile.domain.Friend
import com.kglabs28.yapnplay.appengine.features.playrooms.domain.PlayRoom

/**
 * Rooms tab: create/join on the left, the user's rooms and online
 * friends on the right. Lists stay empty until [PlayRoomsIntent.Load] fills
 * them from the repository; [loaded] lets the UI fade them in and only
 * then show the empty-state text.
 */
data class PlayRoomsState(
    val rooms: List<PlayRoom> = emptyList(),
    val onlineFriends: List<Friend> = emptyList(),
    val loaded: Boolean = false,
    val roomCode: String = "",
    val isJoining: Boolean = false,
    val joinError: Boolean = false,
) : UiState

sealed interface PlayRoomsIntent : UiIntent {
    data object Load : PlayRoomsIntent
    data class RoomCodeChanged(val code: String) : PlayRoomsIntent
    data object JoinRoom : PlayRoomsIntent
    data object CreateRoom : PlayRoomsIntent
    data class OpenRoom(val roomId: String) : PlayRoomsIntent
    data object SeeAllRooms : PlayRoomsIntent
}

sealed interface PlayRoomsEffect : UiEffect {
    data class OpenRoom(val roomId: String) : PlayRoomsEffect
    data object OpenCreateRoom : PlayRoomsEffect
    data object OpenAllRooms : PlayRoomsEffect
}
