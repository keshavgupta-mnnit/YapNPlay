package com.kglabs28.yapnplay.appengine.ui.screens.playrooms

import com.kglabs28.yapnplay.appengine.core.mvi.UiEffect
import com.kglabs28.yapnplay.appengine.core.mvi.UiIntent
import com.kglabs28.yapnplay.appengine.core.mvi.UiState
import com.kglabs28.yapnplay.appengine.features.playrooms.domain.PlayRoomIcon
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
    /** "Create New Group" while it's open. */
    val createGroup: CreateGroupUi? = null,
    /** "Group Created!" with its link, after creating. */
    val groupCreated: GroupCreatedUi? = null,
    /** Group invites waiting for the user (the INVITES button and its dialog). */
    val invites: List<GroupInviteUi> = emptyList(),
    val invitesOpen: Boolean = false,
    val inviteError: String? = null,
) : UiState

/** The Create New Group form. [selectedIds]: friends to invite; the user is always the host. */
data class CreateGroupUi(
    val name: String = "",
    val icon: PlayRoomIcon = PlayRoomIcon.GROUP,
    val friends: List<Friend> = emptyList(),
    val selectedIds: List<String> = emptyList(),
    val search: String = "",
    val hostName: String = "",
    val hostAvatarId: String = "",
    val creating: Boolean = false,
    val error: String? = null,
)

/** A group just created: its code and invite link to share. */
data class GroupCreatedUi(val roomId: String, val name: String, val code: String, val link: String, val body: String, val shareText: String)

/** One group invite in the list. */
data class GroupInviteUi(val inviteId: String, val roomName: String, val icon: PlayRoomIcon, val detail: String, val inviterAvatarId: String)

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
    /** From a friend's profile card: Create New Group with them already picked. */
    data class CreateGroupWith(val userId: String) : PlayRoomsIntent
    data class GroupNameChanged(val name: String) : PlayRoomsIntent
    data object CycleGroupIcon : PlayRoomsIntent
    data class ToggleGroupMember(val userId: String) : PlayRoomsIntent
    data class GroupSearchChanged(val text: String) : PlayRoomsIntent
    data object SubmitGroup : PlayRoomsIntent
    data object DismissCreateGroup : PlayRoomsIntent
    data object ShareGroup : PlayRoomsIntent
    data object OpenCreatedGroup : PlayRoomsIntent
    data object DismissGroupCreated : PlayRoomsIntent
    data object OpenInvites : PlayRoomsIntent
    data object DismissInvites : PlayRoomsIntent
    data class AcceptInvite(val inviteId: String) : PlayRoomsIntent
    data class DeclineInvite(val inviteId: String) : PlayRoomsIntent
    data class OpenRoom(val roomId: String) : PlayRoomsIntent
    data object SeeAllRooms : PlayRoomsIntent
}

sealed interface PlayRoomsEffect : UiEffect {
    data class OpenRoom(val roomId: String) : PlayRoomsEffect
    data object OpenCreateRoom : PlayRoomsEffect
    data object OpenAllRooms : PlayRoomsEffect
    data class ShareText(val text: String) : PlayRoomsEffect
}
