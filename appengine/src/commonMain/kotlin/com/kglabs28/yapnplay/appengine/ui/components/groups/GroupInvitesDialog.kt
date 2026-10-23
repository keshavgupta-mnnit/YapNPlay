package com.kglabs28.yapnplay.appengine.ui.components.groups

import com.kglabs28.yapnplay.appengine.ui.components.common.ShrinkText
import com.kglabs28.yapnplay.appengine.core.ui.RoomStrings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.ui.components.common.EmptyStateText
import com.kglabs28.yapnplay.appengine.ui.components.common.IconCircle
import com.kglabs28.yapnplay.appengine.ui.components.common.glassScrollbar
import com.kglabs28.yapnplay.appengine.ui.components.playrooms.badge
import com.kglabs28.yapnplay.appengine.ui.components.profile.ProfileActionButton
import com.kglabs28.yapnplay.appengine.ui.components.profile.ProfileButtonKind
import com.kglabs28.yapnplay.appengine.ui.screens.playrooms.GroupInviteUi

/** "Group Invites": each group the user was invited to (who invited, how many members), Decline or Accept. */
@Composable
fun GroupInvitesDialog(
    invites: List<GroupInviteUi>,
    error: String?,
    onAccept: (inviteId: String) -> Unit,
    onDecline: (inviteId: String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val typography = YapNPlayTheme.typography
    GroupDialogFrame(width = dimens.profileWideDialogWidth, modifier = modifier) {
        GroupDialogTitle(start = "", end = RoomStrings.groupInvitesTitle, onClose = onClose)
        error?.let { BasicText(text = it, style = typography.micro.copy(color = colors.Error)) }
        if (invites.isEmpty()) {
            EmptyStateText(text = RoomStrings.groupNoInvites, modifier = Modifier.fillMaxWidth())
        } else {
            val scroll = rememberScrollState()
            Column(
                modifier = Modifier.fillMaxWidth().heightIn(max = dimens.groupListMaxHeight).glassScrollbar(scroll).verticalScroll(scroll).padding(end = dimens.scrollbarWidth * 2),
                verticalArrangement = Arrangement.spacedBy(Spacing.Xs),
            ) {
                invites.forEach { invite ->
                    val badge = invite.icon.badge()
                    Row(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(dimens.cbRowRadius)).background(colors.ListRow).padding(Spacing.Xs),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.Sm),
                    ) {
                        IconCircle(imageVector = badge.icon, fill = badge.fill, size = dimens.personRowAvatar, iconSize = dimens.iconMedium)
                        Column(modifier = Modifier.weight(1f)) {
                            ShrinkText(text = invite.roomName, style = typography.label.copy(color = colors.TextPrimary, fontWeight = FontWeight.Bold))
                            BasicText(text = invite.detail, style = typography.micro.copy(color = colors.TextSecondary), maxLines = 1)
                        }
                        ProfileActionButton(text = RoomStrings.groupDecline, kind = ProfileButtonKind.NEUTRAL, onClick = { onDecline(invite.inviteId) }, height = dimens.smallButtonHeight)
                        ProfileActionButton(text = RoomStrings.groupAccept, kind = ProfileButtonKind.CONFIRM, onClick = { onAccept(invite.inviteId) }, height = dimens.smallButtonHeight)
                    }
                }
            }
        }
    }
}
