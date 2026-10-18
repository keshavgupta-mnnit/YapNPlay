package com.kglabs28.yapnplay.appengine.ui.screens.profile

import com.kglabs28.yapnplay.appengine.core.mvi.UiEffect
import com.kglabs28.yapnplay.appengine.core.mvi.UiIntent
import com.kglabs28.yapnplay.appengine.core.mvi.UiState
import com.kglabs28.yapnplay.appengine.features.profile.domain.Friend

/**
 * The user's profile: who they are (avatar, name, @username, their ID and
 * a few stats), their friends (online first), adding a friend by ID and
 * the friend requests waiting for them.
 */
data class ProfileState(
    val loaded: Boolean = false,
    val name: String = "",
    /** "@keshav_28" (not unique). */
    val username: String = "",
    /** The raw 6-character ID, for copying. */
    val userCode: String = "",
    val avatarId: String = "",
    val stats: List<ProfileStatUi> = emptyList(),
    /** Online friends first, then offline; each group by name. */
    val friends: List<Friend> = emptyList(),
    val friendsSubtitle: String = "",
    val requests: List<ProfileRequestUi> = emptyList(),
    val requestsLabel: String = "",
    /** The ID typed into "Add a friend", and the line under its title (hint, result or error). */
    val addCode: String = "",
    val addMessage: String? = null,
    val addError: Boolean = false,
    val sending: Boolean = false,
    /** "Copied" shows for a moment after copying the ID. */
    val codeCopied: Boolean = false,
    /** Logging out needs a sign-in to come back; hidden while accounts are guest-only. */
    val canLogOut: Boolean = false,
    val modal: ProfileModal? = null,
) : UiState

data class ProfileStatUi(val value: String, val label: String)

/** Someone shown on a frosted person card: a friend, or someone asking to be one. */
data class ProfilePersonUi(
    val userId: String,
    val name: String,
    val username: String,
    val userCode: String,
    val avatarId: String,
    val online: Boolean,
    val status: String,
)

data class ProfileRequestUi(val requestId: String, val person: ProfilePersonUi)

enum class ProfileField { NAME, USERNAME }

sealed interface ProfileModal {
    data class AvatarPicker(val selected: String, val saving: Boolean = false) : ProfileModal
    data class EditText(
        val field: ProfileField,
        val title: String,
        val value: String,
        val hint: String,
        val maxLength: Int,
        val error: String? = null,
        val saving: Boolean = false,
    ) : ProfileModal
    data object AllFriends : ProfileModal
    /** A friend's card: Block and Remove. */
    data class Person(val person: ProfilePersonUi) : ProfileModal
    /** Everyone asking to be friends: Accept or Decline each. */
    data object Requests : ProfileModal
    data class ConfirmRemove(val person: ProfilePersonUi) : ProfileModal
    data class ConfirmBlock(val person: ProfilePersonUi) : ProfileModal
    data object ConfirmLogout : ProfileModal
}

sealed interface ProfileIntent : UiIntent {
    data object Load : ProfileIntent
    data object Back : ProfileIntent
    data object EditAvatar : ProfileIntent
    data class PickAvatar(val avatarId: String) : ProfileIntent
    data object SaveAvatar : ProfileIntent
    data object EditName : ProfileIntent
    data object EditUsername : ProfileIntent
    data class EditTextChanged(val text: String) : ProfileIntent
    data object SaveText : ProfileIntent
    /** The screen copied the user's ID to the clipboard. */
    data object CodeCopied : ProfileIntent
    data class FriendClicked(val userId: String) : ProfileIntent
    data object SeeAllFriends : ProfileIntent
    data class RemoveFriend(val userId: String) : ProfileIntent
    data class BlockUser(val userId: String) : ProfileIntent
    data object ConfirmAction : ProfileIntent
    data class AddCodeChanged(val code: String) : ProfileIntent
    data object SendRequest : ProfileIntent
    data object ViewRequests : ProfileIntent
    data class AcceptRequest(val requestId: String) : ProfileIntent
    data class DeclineRequest(val requestId: String) : ProfileIntent
    data object Logout : ProfileIntent
    data object DismissModal : ProfileIntent
}

sealed interface ProfileEffect : UiEffect {
    data object Close : ProfileEffect
}
