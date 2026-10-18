package com.kglabs28.yapnplay.appengine.ui.components.profile

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.features.profile.domain.Friend
import com.kglabs28.yapnplay.appengine.ui.screens.profile.ProfileModal
import com.kglabs28.yapnplay.appengine.ui.screens.profile.ProfileRequestUi

/** The dialog for [modal]: avatar picker, edit name / username, friends grid, a friend's card, requests, confirmations. */
@Composable
fun ProfileModalContent(
    modal: ProfileModal,
    friends: List<Friend>,
    requests: List<ProfileRequestUi>,
    actions: ProfileModalActions,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    when (modal) {
        is ProfileModal.AvatarPicker -> AvatarPickerCard(
            selected = modal.selected,
            saving = modal.saving,
            onPick = actions.onPickAvatar,
            onSave = actions.onSaveAvatar,
            onClose = actions.onDismiss,
            modifier = modifier,
        )
        is ProfileModal.EditText -> ProfileEditCard(
            title = modal.title,
            value = modal.value,
            hint = modal.hint,
            maxLength = modal.maxLength,
            error = modal.error,
            saving = modal.saving,
            onValueChange = actions.onEditTextChanged,
            onSave = actions.onSaveText,
            onClose = actions.onDismiss,
            modifier = modifier,
        )
        ProfileModal.AllFriends -> AllFriendsCard(friends = friends, onFriendClick = actions.onFriendClick, onClose = actions.onDismiss, modifier = modifier)
        is ProfileModal.Person -> PersonCard(person = modal.person, modifier = modifier) {
            TintedGlassButton(text = Strings.profileBlock, tint = colors.IconMuted, onClick = { actions.onBlock(modal.person.userId) }, modifier = Modifier.weight(1f))
            TintedGlassButton(text = Strings.profileRemove, tint = colors.Error, onClick = { actions.onRemove(modal.person.userId) }, modifier = Modifier.weight(1f))
        }
        ProfileModal.Requests -> FriendRequestsListCard(
            requests = requests,
            onAccept = actions.onAccept,
            onDecline = actions.onDecline,
            onClose = actions.onDismiss,
            modifier = modifier,
        )
        is ProfileModal.ConfirmRemove -> ProfileConfirmCard(
            title = Strings.profileRemoveTitle(modal.person.name),
            body = Strings.profileRemoveBody,
            confirmLabel = Strings.profileRemove,
            confirmTint = colors.Error,
            onConfirm = actions.onConfirm,
            onCancel = actions.onDismiss,
            modifier = modifier,
        )
        is ProfileModal.ConfirmBlock -> ProfileConfirmCard(
            title = Strings.profileBlockTitle(modal.person.name),
            body = Strings.profileBlockBody,
            confirmLabel = Strings.profileBlock,
            confirmTint = colors.Error,
            onConfirm = actions.onConfirm,
            onCancel = actions.onDismiss,
            modifier = modifier,
        )
        ProfileModal.ConfirmLogout -> ProfileConfirmCard(
            title = Strings.profileLogoutTitle,
            body = Strings.profileLogoutBody,
            confirmLabel = Strings.profileLogout,
            confirmTint = colors.Error,
            onConfirm = actions.onConfirm,
            onCancel = actions.onDismiss,
            modifier = modifier,
        )
    }
}
