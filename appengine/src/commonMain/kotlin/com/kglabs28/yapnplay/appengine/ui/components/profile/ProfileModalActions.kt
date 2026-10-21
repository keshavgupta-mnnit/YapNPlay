package com.kglabs28.yapnplay.appengine.ui.components.profile

import com.kglabs28.yapnplay.appengine.ui.screens.profile.FriendsTab

/** What the profile's dialogs can do. */
data class ProfileModalActions(
    val onDismiss: () -> Unit,
    val onPickAvatar: (String) -> Unit,
    val onSaveAvatar: () -> Unit,
    val onEditTextChanged: (String) -> Unit,
    val onSaveText: () -> Unit,
    val onSelectAllFriendsTab: (FriendsTab) -> Unit,
    val onFriendClick: (userId: String) -> Unit,
    val onRemove: (userId: String) -> Unit,
    val onBlock: (userId: String) -> Unit,
    val onConfirm: () -> Unit,
    val onOpenRequest: (requestId: String) -> Unit,
    val onAccept: (requestId: String) -> Unit,
    val onDecline: (requestId: String) -> Unit,
    val onUnblock: (userId: String) -> Unit,
    val onCopyCode: (code: String) -> Unit,
    val onInviteToGroup: (userId: String) -> Unit,
    val onInviteToRoom: (roomId: String) -> Unit,
    val onNewGroup: () -> Unit,
)
