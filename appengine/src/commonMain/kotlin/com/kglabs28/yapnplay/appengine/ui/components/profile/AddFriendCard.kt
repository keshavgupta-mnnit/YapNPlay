package com.kglabs28.yapnplay.appengine.ui.components.profile

import com.kglabs28.yapnplay.appengine.core.ui.ProfileStrings
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme

/**
 * "Add Friend": the line under the title is the hint, or the result
 * ("Request sent", "No one has that ID"…); then the ID field (a tap opens
 * the in-app keyboard) and the green Add Friend button.
 */
@Composable
fun AddFriendCard(
    code: String,
    message: String?,
    isError: Boolean,
    sending: Boolean,
    keyboardOpen: Boolean,
    onOpenKeyboard: () -> Unit,
    onSend: () -> Unit,
    modifier: Modifier = Modifier,
    onClearCode: () -> Unit = {},
) {
    val colors = YapNPlayTheme.colors
    val ready = code.length == AppConstants.USER_CODE_LENGTH && !sending
    ProfileActionCard(
        icon = Icons.Rounded.PersonAdd,
        iconTint = colors.Cyan,
        title = ProfileStrings.profileAddTitle,
        description = message ?: ProfileStrings.profileAddHint(AppConstants.USER_CODE_LENGTH),
        descriptionColor = when {
            message == null -> colors.RoomsSubtitle
            isError -> colors.Error
            else -> colors.LiveGreen
        },
        modifier = modifier,
    ) {
        FriendCodeField(
            code = code,
            isError = isError && message != null,
            active = keyboardOpen,
            onActivate = onOpenKeyboard,
            onClear = onClearCode,
            modifier = Modifier.weight(1f),
        )
        ProfileActionButton(
            text = ProfileStrings.profileAddButton,
            kind = ProfileButtonKind.CONFIRM,
            onClick = onSend,
            enabled = ready,
            height = YapNPlayTheme.dimens.smallButtonHeight,
        )
    }
}
