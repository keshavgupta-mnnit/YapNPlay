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
    /** Why the last join failed, ready to show; null when there is no error. */
    val joinError: String? = null,
    /** "Join this room?" before joining (typed code or invite link). */
    val joinPrompt: JoinPromptUi? = null,
    /** In-app keypad replaces rooms + friends while the code is being typed. */
    val keypadOpen: Boolean = false,
    /** "Request sent, the admin has to approve" after joining someone's room. */
    val requestSent: RequestSentUi? = null,
) : UiState

/** The waiting-for-approval note, ready to show. */
data class RequestSentUi(val title: String, val body: String)

/** The join confirmation, ready to show. */
data class JoinPromptUi(
    val code: String,
    val title: String,
    val details: String,
    val description: String,
)

sealed interface PlayRoomsIntent : UiIntent {
    data object Load : PlayRoomsIntent
    data class RoomCodeChanged(val code: String) : PlayRoomsIntent
    /** JOIN pressed: look the code up, then ask "Join this room?". */
    data object JoinRoom : PlayRoomsIntent
    /** Code field tapped: show the keypad. */
    data object OpenKeypad : PlayRoomsIntent
    /** Tap elsewhere, Back or the cross: hide it. */
    data object CloseKeypad : PlayRoomsIntent
    data object DismissRequestSent : PlayRoomsIntent
    data class KeypadDigit(val digit: Char) : PlayRoomsIntent
    data object KeypadBackspace : PlayRoomsIntent
    data object ClearCode : PlayRoomsIntent
    /** Opened from an invite link: same lookup and question. */
    data class PreviewInvite(val code: String) : PlayRoomsIntent
    data object ConfirmJoin : PlayRoomsIntent
    data object DismissJoinPrompt : PlayRoomsIntent
    data object CreateRoom : PlayRoomsIntent
    data class OpenRoom(val roomId: String) : PlayRoomsIntent
    data object SeeAllRooms : PlayRoomsIntent
}

sealed interface PlayRoomsEffect : UiEffect {
    data class OpenRoom(val roomId: String) : PlayRoomsEffect
    data object OpenCreateRoom : PlayRoomsEffect
    data object OpenAllRooms : PlayRoomsEffect
}
