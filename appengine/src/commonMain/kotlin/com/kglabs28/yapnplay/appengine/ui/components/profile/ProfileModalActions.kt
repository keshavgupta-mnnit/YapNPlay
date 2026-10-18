package com.kglabs28.yapnplay.appengine.ui.components.profile

/** What the profile's dialogs can do. */
data class ProfileModalActions(
    val onDismiss: () -> Unit,
    val onPickAvatar: (String) -> Unit,
    val onSaveAvatar: () -> Unit,
    val onEditTextChanged: (String) -> Unit,
    val onSaveText: () -> Unit,
    val onFriendClick: (userId: String) -> Unit,
    val onRemove: (userId: String) -> Unit,
    val onBlock: (userId: String) -> Unit,
    val onConfirm: () -> Unit,
    val onAccept: (requestId: String) -> Unit,
    val onDecline: (requestId: String) -> Unit,
)
