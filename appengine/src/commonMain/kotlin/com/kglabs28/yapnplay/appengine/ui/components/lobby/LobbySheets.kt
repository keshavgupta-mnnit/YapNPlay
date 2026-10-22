package com.kglabs28.yapnplay.appengine.ui.components.lobby

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.RoomStrings
import com.kglabs28.yapnplay.appengine.ui.components.common.AppIcon
import com.kglabs28.yapnplay.appengine.ui.components.common.ModalCard
import com.kglabs28.yapnplay.appengine.ui.components.common.PillButton
import com.kglabs28.yapnplay.appengine.ui.components.playrooms.FriendAvatarItem
import com.kglabs28.yapnplay.appengine.ui.components.profile.ProfileTextField
import com.kglabs28.yapnplay.appengine.ui.screens.playroomlobby.LobbySheet

/**
 * Add Friends to the group, from the ⋮ menu. Two rows: a box for a
 * player's code (anyone, friend or not), then the user's friends who
 * aren't in the group yet as the usual sideways row of avatars — a tap
 * picks one (ticked). SEND INVITES invites the code and everyone picked.
 */
@Composable
fun LobbyAddFriendsDialog(
    sheet: LobbySheet.AddFriends,
    onCodeChange: (String) -> Unit,
    onToggle: (userId: String) -> Unit,
    onSend: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    ModalCard(
        title = RoomStrings.lobbyAddFriendsTitle,
        onClose = onClose,
        modifier = modifier,
        actions = {
            PillButton(
                text = RoomStrings.lobbyAddFriendsSend,
                onClick = onSend,
                enabled = sheet.canSend && !sheet.sending,
                gradient = listOf(colors.CtaGreenStart, colors.CtaGreenMid, colors.CtaGreenEnd),
                contentColor = colors.CtaGreenContent,
                trailingIcon = null,
            )
        },
    ) {
        // Row 1: a player's code.
        ProfileTextField(
            value = sheet.code,
            onValueChange = onCodeChange,
            hint = RoomStrings.lobbyAddFriendsCodeHint,
            maxLength = AppConstants.USER_CODE_LENGTH,
            isError = false,
            onDone = onSend,
            modifier = Modifier.fillMaxWidth(),
        )
        // Row 2: friends not in the group yet, sideways.
        Box(modifier = Modifier.fillMaxWidth().height(dimens.lobbyFriendsRowHeight), contentAlignment = Alignment.CenterStart) {
            when {
                sheet.loading -> Unit
                sheet.friends.isEmpty() -> BasicText(text = RoomStrings.lobbyAddFriendsEmpty, style = YapNPlayTheme.typography.label.copy(color = colors.TextSecondary))
                else -> LazyRow(horizontalArrangement = Arrangement.spacedBy(Spacing.Sm), verticalAlignment = Alignment.CenterVertically) {
                    items(sheet.friends, key = { it.userId }) { friend ->
                        val picked = friend.userId in sheet.selected
                        Box(contentAlignment = Alignment.TopEnd) {
                            FriendAvatarItem(
                                friend = friend,
                                onClick = { onToggle(friend.userId) },
                                modifier = if (picked) Modifier.clip(YapNPlayTheme.shapes.button).border(dimens.borderThin, colors.CtaGreenStart, YapNPlayTheme.shapes.button) else Modifier,
                            )
                            if (picked) AppIcon(imageVector = Icons.Rounded.CheckCircle, tint = colors.CtaGreenStart, size = dimens.iconSmall, modifier = Modifier.clip(CircleShape))
                        }
                    }
                }
            }
        }
        sheet.error?.let { BasicText(text = it, style = YapNPlayTheme.typography.label.copy(color = colors.Error)) }
    }
}

/** Edit Name, from the ⋮ menu (admins): the group's name and SAVE. */
@Composable
fun LobbyEditNameDialog(
    sheet: LobbySheet.EditName,
    onNameChange: (String) -> Unit,
    onSave: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    ModalCard(
        title = RoomStrings.lobbyEditNameTitle,
        onClose = onClose,
        modifier = modifier,
        actions = {
            PillButton(
                text = RoomStrings.lobbyEditNameSave,
                onClick = onSave,
                enabled = sheet.name.isNotBlank(),
                gradient = listOf(colors.CtaGreenStart, colors.CtaGreenMid, colors.CtaGreenEnd),
                contentColor = colors.CtaGreenContent,
                trailingIcon = null,
            )
        },
    ) {
        ProfileTextField(
            value = sheet.name,
            onValueChange = onNameChange,
            hint = RoomStrings.groupNameHint,
            maxLength = AppConstants.MAX_ROOM_NAME_LENGTH,
            isError = false,
            onDone = onSave,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
