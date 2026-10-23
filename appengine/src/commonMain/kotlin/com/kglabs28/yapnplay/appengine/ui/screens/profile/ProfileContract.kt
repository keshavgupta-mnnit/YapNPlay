package com.kglabs28.yapnplay.appengine.ui.screens.profile

import com.kglabs28.yapnplay.appengine.ui.components.moderation.AppSettingsUi
import com.kglabs28.yapnplay.appengine.core.mvi.UiEffect
import com.kglabs28.yapnplay.appengine.core.mvi.UiIntent
import com.kglabs28.yapnplay.appengine.core.mvi.UiState
import com.kglabs28.yapnplay.appengine.features.playrooms.domain.PlayRoomIcon
import com.kglabs28.yapnplay.appengine.features.profile.domain.Friend

/**
 * The user's profile: who they are (avatar, name, YapNPlay ID,
 * stats with their level), their friends (Online / Offline tabs, slider,
 * See All), then Add Friend, Friend Requests and Blocked Users.
 */
data class ProfileState(
    /** The settings dialog (sound, music, match history); null when closed. */
    val settings: AppSettingsUi? = null,
    val loaded: Boolean = false,
    val name: String = "",
    /** The raw 6-character YapNPlay ID, for showing and copying. */
    val userCode: String = "",
    val avatarId: String = "",
    val stats: List<ProfileStatUi> = emptyList(),
    /** Every friend, online first. */
    val friends: List<Friend> = emptyList(),
    val onlineCount: Int = 0,
    val offlineCount: Int = 0,
    /** My Friends: which tab, and the friends it shows. */
    val friendsTab: FriendsTab = FriendsTab.ONLINE,
    val tabFriends: List<Friend> = emptyList(),
    /** See All: which tab, and the friends it shows. */
    val allFriendsTab: FriendsTab = FriendsTab.ALL,
    val allTabFriends: List<Friend> = emptyList(),
    val requests: List<ProfileRequestUi> = emptyList(),
    val requestsLabel: String = "",
    val blocked: List<ProfileBlockedUi> = emptyList(),
    val blockedLabel: String = "",
    /** The ID typed into Add Friend, and the line under its title (hint, result or error). */
    val addCode: String = "",
    val addMessage: String? = null,
    val addError: Boolean = false,
    val sending: Boolean = false,
    /** The in-app ID keyboard is open (it takes the place of Friend Requests and Blocked Users). */
    val keyboardOpen: Boolean = false,
    /** "Copied" shows for a moment after copying the ID. */
    val codeCopied: Boolean = false,
    val modal: ProfileModal? = null,
) : UiState

enum class FriendsTab { ALL, ONLINE, OFFLINE }

/** One of the user's groups in "Invite to Group". [invited]: an invite went out from here. */
data class InviteGroupUi(val roomId: String, val name: String, val icon: PlayRoomIcon, val detail: String, val invited: Boolean = false, val sending: Boolean = false)

enum class ProfileStatKind { GAMES, WINS, FRIENDS, LEVEL }

data class ProfileStatUi(val kind: ProfileStatKind, val value: String, val label: String)

/** Someone on a profile card: a friend, or someone asking to be one. [userCode] is raw. */
data class ProfilePersonUi(
    val userId: String,
    val name: String,
    val userCode: String,
    val avatarId: String,
    val online: Boolean,
    val status: String,
)

data class ProfileRequestUi(val requestId: String, val person: ProfilePersonUi)

data class ProfileBlockedUi(val userId: String, val name: String, val avatarId: String, val blockedOn: String)

sealed interface ProfileModal {
    data class AvatarPicker(val selected: String, val saving: Boolean = false) : ProfileModal
    /** Edit Name. */
    data class EditText(
        val title: String,
        val label: String,
        val value: String,
        val hint: String,
        val maxLength: Int,
        val error: String? = null,
        val saving: Boolean = false,
    ) : ProfileModal
    data object AllFriends : ProfileModal
    /** A friend's card: Remove Friend and Block User. */
    data class Person(val person: ProfilePersonUi) : ProfileModal
    /** "Invite <friend>": the user's groups (Invite / Invited each), or New Group with them. */
    data class InviteToGroup(val person: ProfilePersonUi, val groups: List<InviteGroupUi> = emptyList(), val loading: Boolean = true, val error: String? = null) : ProfileModal
    /** Everyone asking to be friends: Decline or Accept each; tap one for their card. */
    data object Requests : ProfileModal
    data class RequestProfile(val request: ProfileRequestUi) : ProfileModal
    data class RequestAccepted(val name: String) : ProfileModal
    data class ConfirmRemove(val person: ProfilePersonUi) : ProfileModal
    data class ConfirmBlock(val person: ProfilePersonUi) : ProfileModal
    data object BlockList : ProfileModal
    data class ConfirmUnblock(val user: ProfileBlockedUi) : ProfileModal
}

sealed interface ProfileIntent : UiIntent {
    data object Load : ProfileIntent
    data object Back : ProfileIntent
    data object OpenSettings : ProfileIntent
    data object CloseSettings : ProfileIntent
    data object ToggleEffects : ProfileIntent
    data object ToggleMusic : ProfileIntent
    /** Match History in the settings. */
    data object OpenHistory : ProfileIntent
    data object EditAvatar : ProfileIntent
    data class PickAvatar(val avatarId: String) : ProfileIntent
    data object SaveAvatar : ProfileIntent
    data object EditName : ProfileIntent
    data class EditTextChanged(val text: String) : ProfileIntent
    data object SaveText : ProfileIntent
    /** The screen copied the user's ID to the clipboard. */
    data object CodeCopied : ProfileIntent
    data class SelectFriendsTab(val tab: FriendsTab) : ProfileIntent
    data class SelectAllFriendsTab(val tab: FriendsTab) : ProfileIntent
    data class FriendClicked(val userId: String) : ProfileIntent
    data object SeeAllFriends : ProfileIntent
    data class RemoveFriend(val userId: String) : ProfileIntent
    data class BlockUser(val userId: String) : ProfileIntent
    data class InviteToGroup(val userId: String) : ProfileIntent
    data class InviteToRoom(val roomId: String) : ProfileIntent
    data object NewGroupWithFriend : ProfileIntent
    data object ConfirmAction : ProfileIntent
    data object OpenKeyboard : ProfileIntent
    data object CloseKeyboard : ProfileIntent
    data class KeyTyped(val key: Char) : ProfileIntent
    data object KeyBackspace : ProfileIntent
    data object ClearCode : ProfileIntent
    data object SendRequest : ProfileIntent
    data object ViewRequests : ProfileIntent
    data class OpenRequest(val requestId: String) : ProfileIntent
    data class AcceptRequest(val requestId: String) : ProfileIntent
    data class DeclineRequest(val requestId: String) : ProfileIntent
    data object ViewBlocked : ProfileIntent
    data class Unblock(val userId: String) : ProfileIntent
    data object DismissModal : ProfileIntent
}

sealed interface ProfileEffect : UiEffect {
    data object Close : ProfileEffect
    data object OpenHistory : ProfileEffect
    /** Go to the Rooms tab's Create New Group with this friend picked. */
    data class OpenCreateGroup(val userId: String) : ProfileEffect
}
