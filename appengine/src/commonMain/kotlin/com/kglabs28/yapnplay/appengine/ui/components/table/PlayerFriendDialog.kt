package com.kglabs28.yapnplay.appengine.ui.components.table

import com.kglabs28.yapnplay.appengine.core.ui.TableStrings
import com.kglabs28.yapnplay.appengine.core.ui.ProfileStrings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
            title = TableStrings.playerRemoveFriendTitle,
            avatarId = action.avatarId,
            question = ProfileStrings.profileRemoveQuestion(action.name),
            body = action.error ?: ProfileStrings.profileRemoveBody,
            confirmLabel = ProfileStrings.profileRemove,
            onConfirm = confirm,
            onCancel = onCancel,
            modifier = modifier,
        )
    } else {
        ConfirmPersonCard(
            title = TableStrings.playerAddFriendTitle,
            avatarId = action.avatarId,
            question = TableStrings.playerAddFriendQuestion(action.name),
            body = action.error ?: TableStrings.playerAddFriendBody,
            confirmLabel = TableStrings.playerAddFriendConfirm,
            onConfirm = confirm,
            onCancel = onCancel,
            confirmKind = ProfileButtonKind.CONFIRM,
            modifier = modifier,
        )
    }
}
