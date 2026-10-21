package com.kglabs28.yapnplay.appengine.ui.components.profile

import com.kglabs28.yapnplay.appengine.core.ui.ProfileStrings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.ui.screens.profile.ProfileModal
import com.kglabs28.yapnplay.appengine.ui.screens.profile.ProfileState

/** The dialog for [modal], as in the design. */
@Composable
fun ProfileModalContent(
    modal: ProfileModal,
    state: ProfileState,
    actions: ProfileModalActions,
    modifier: Modifier = Modifier,
) {
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
            label = modal.label,
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
        ProfileModal.AllFriends -> AllFriendsCard(
            friends = state.allTabFriends,
            tab = state.allFriendsTab,
            allCount = state.friends.size,
            onlineCount = state.onlineCount,
            offlineCount = state.offlineCount,
            onSelectTab = actions.onSelectAllFriendsTab,
            onFriendClick = actions.onFriendClick,
            onClose = actions.onDismiss,
            modifier = modifier,
        )
        is ProfileModal.Person -> FriendProfileCard(
            person = modal.person,
            onCopyCode = { actions.onCopyCode(modal.person.userCode) },
            onRemove = { actions.onRemove(modal.person.userId) },
            onBlock = { actions.onBlock(modal.person.userId) },
            onInviteToGroup = { actions.onInviteToGroup(modal.person.userId) },
            onClose = actions.onDismiss,
            modifier = modifier,
        )
        is ProfileModal.InviteToGroup -> InviteToGroupCard(
            modal = modal,
            onInvite = actions.onInviteToRoom,
            onNewGroup = actions.onNewGroup,
            onClose = actions.onDismiss,
            modifier = modifier,
        )
        ProfileModal.Requests -> FriendRequestsListCard(
            requests = state.requests,
            onOpen = actions.onOpenRequest,
            onAccept = actions.onAccept,
            onDecline = actions.onDecline,
            onClose = actions.onDismiss,
            modifier = modifier,
        )
        is ProfileModal.RequestProfile -> RequestProfileCard(
            request = modal.request,
            onCopyCode = { actions.onCopyCode(modal.request.person.userCode) },
            onAccept = { actions.onAccept(modal.request.requestId) },
            onClose = actions.onDismiss,
            modifier = modifier,
        )
        is ProfileModal.RequestAccepted -> RequestAcceptedCard(name = modal.name, onDone = actions.onDismiss, modifier = modifier)
        is ProfileModal.ConfirmRemove -> ConfirmPersonCard(
            title = ProfileStrings.profileRemoveDialogTitle,
            avatarId = modal.person.avatarId,
            question = ProfileStrings.profileRemoveQuestion(modal.person.name),
            body = ProfileStrings.profileRemoveBody,
            confirmLabel = ProfileStrings.profileRemove,
            onConfirm = actions.onConfirm,
            onCancel = actions.onDismiss,
            modifier = modifier,
        )
        is ProfileModal.ConfirmBlock -> ConfirmPersonCard(
            title = ProfileStrings.profileBlockDialogTitle,
            avatarId = modal.person.avatarId,
            question = ProfileStrings.profileBlockQuestion(modal.person.name),
            body = ProfileStrings.profileBlockBody,
            confirmLabel = ProfileStrings.profileBlock,
            onConfirm = actions.onConfirm,
            onCancel = actions.onDismiss,
            modifier = modifier,
        )
        is ProfileModal.ConfirmUnblock -> ConfirmPersonCard(
            title = ProfileStrings.profileUnblockDialogTitle,
            avatarId = modal.user.avatarId,
            question = ProfileStrings.profileUnblockQuestion(modal.user.name),
            body = ProfileStrings.profileUnblockBody,
            confirmLabel = ProfileStrings.profileUnblock,
            onConfirm = actions.onConfirm,
            onCancel = actions.onDismiss,
            confirmKind = ProfileButtonKind.CONFIRM,
            modifier = modifier,
        )
        ProfileModal.BlockList -> BlockListCard(blocked = state.blocked, onUnblock = actions.onUnblock, onClose = actions.onDismiss, modifier = modifier)
    }
}
