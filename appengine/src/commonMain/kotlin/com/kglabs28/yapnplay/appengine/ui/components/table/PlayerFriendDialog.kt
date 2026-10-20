package com.kglabs28.yapnplay.appengine.ui.components.table

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.ui.components.profile.ConfirmPersonCard
import com.kglabs28.yapnplay.appengine.ui.components.profile.ProfileButtonKind

/** "Add Friend" (green Send Request) or "Remove Friend" (red Remove) for a player at the table. */
@Composable
fun PlayerFriendDialog(
    action: PlayerFriendActionUi,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val confirm = if (action.busy) ({}) else onConfirm
    if (action.isFriend) {
        ConfirmPersonCard(
            title = Strings.playerRemoveFriendTitle,
            avatarId = action.avatarId,
            question = Strings.profileRemoveQuestion(action.name),
            body = action.error ?: Strings.profileRemoveBody,
            confirmLabel = Strings.profileRemove,
            onConfirm = confirm,
            onCancel = onCancel,
            modifier = modifier,
        )
    } else {
        ConfirmPersonCard(
            title = Strings.playerAddFriendTitle,
            avatarId = action.avatarId,
            question = Strings.playerAddFriendQuestion(action.name),
            body = action.error ?: Strings.playerAddFriendBody,
            confirmLabel = Strings.playerAddFriendConfirm,
            onConfirm = confirm,
            onCancel = onCancel,
            confirmKind = ProfileButtonKind.CONFIRM,
            modifier = modifier,
        )
    }
}
