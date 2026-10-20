package com.kglabs28.yapnplay.appengine.ui.components.groups

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.designsystem.glassPanel
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.features.profile.domain.Friend
import com.kglabs28.yapnplay.appengine.ui.components.common.EmptyStateText
import com.kglabs28.yapnplay.appengine.ui.components.common.glassScrollbar
import com.kglabs28.yapnplay.appengine.ui.components.profile.ProfileActionButton
import com.kglabs28.yapnplay.appengine.ui.components.profile.ProfileButtonKind
import com.kglabs28.yapnplay.appengine.ui.components.profile.ProfileTextField
import com.kglabs28.yapnplay.appengine.ui.screens.playrooms.CreateGroupUi

/**
 * "Create New Group" (the design): the group's icon (tap to change) and
 * name; on the left the members picked so far (the user as Host, × to drop
 * a friend); on the right the friends list with search and + ADD; then
 * Cancel and Create Group.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreateGroupDialog(
    form: CreateGroupUi,
    onNameChange: (String) -> Unit,
    onCycleIcon: () -> Unit,
    onToggleMember: (userId: String) -> Unit,
    onSearchChange: (String) -> Unit,
    onCreate: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val typography = YapNPlayTheme.typography
    val panelShape = RoundedCornerShape(dimens.cardRadius)
    val picked = form.selectedIds.mapNotNull { id -> form.friends.firstOrNull { it.userId == id } }
    val shown = form.friends.filter { form.search.isBlank() || it.name.contains(form.search.trim(), ignoreCase = true) }
    GroupDialogFrame(width = dimens.groupDialogWidth, modifier = modifier) {
        GroupDialogTitle(start = Strings.groupTitleStart, end = Strings.groupTitleEnd, onClose = onClose)
        BasicText(text = Strings.groupNameLabel, style = typography.label.copy(color = colors.TextSecondary))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.Sm)) {
            GroupIconButton(icon = form.icon, onClick = onCycleIcon)
            ProfileTextField(
                value = form.name,
                onValueChange = onNameChange,
                hint = Strings.groupNameHint,
                maxLength = AppConstants.MAX_ROOM_NAME_LENGTH,
                isError = false,
                onDone = {},
                modifier = Modifier.weight(1f),
            )
        }
        Row(modifier = Modifier.fillMaxWidth().height(dimens.groupPanelHeight), horizontalArrangement = Arrangement.spacedBy(Spacing.Sm)) {
            // Who's in so far.
            Column(
                modifier = Modifier.weight(1f).fillMaxHeight().glassPanel(glowColor = colors.SocialZoneAccent, surface = colors.ListRow).padding(Spacing.Xs),
                verticalArrangement = Arrangement.spacedBy(Spacing.Xs),
            ) {
                BasicText(text = Strings.groupSelectedMembers(picked.size + 1, AppConstants.MAX_MEMBERS), style = typography.label.copy(color = colors.TextPrimary))
                val scroll = rememberScrollState()
                FlowRow(
                    modifier = Modifier.weight(1f).fillMaxWidth().glassScrollbar(scroll).verticalScroll(scroll),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.Xs),
                    verticalArrangement = Arrangement.spacedBy(Spacing.Xs),
                ) {
                    SelectedMemberChip(name = form.hostName, avatarId = form.hostAvatarId, isHost = true, onRemove = {})
                    picked.forEach { friend ->
                        SelectedMemberChip(name = friend.name, avatarId = friend.avatarId, isHost = false, onRemove = { onToggleMember(friend.userId) })
                    }
                }
            }
            // Friends to add.
            Column(
                modifier = Modifier.weight(1f).fillMaxHeight().glassPanel(glowColor = colors.SocialZoneAccent, surface = colors.ListRow).padding(Spacing.Xs),
                verticalArrangement = Arrangement.spacedBy(Spacing.Xs),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.Xs)) {
                    BasicText(text = Strings.groupAddFromFriends, style = typography.label.copy(color = colors.TextPrimary), maxLines = 1)
                    ProfileTextField(value = form.search, onValueChange = onSearchChange, hint = Strings.groupSearchHint, maxLength = SEARCH_MAX, isError = false, onDone = {}, modifier = Modifier.weight(1f))
                }
                val scroll = rememberScrollState()
                Column(
                    modifier = Modifier.weight(1f).fillMaxWidth().glassScrollbar(scroll).verticalScroll(scroll).padding(end = dimens.scrollbarWidth * 2),
                    verticalArrangement = Arrangement.spacedBy(Spacing.Xxs),
                ) {
                    when {
                        form.friends.isEmpty() -> EmptyStateText(text = Strings.groupNoFriends, modifier = Modifier.fillMaxWidth())
                        shown.isEmpty() -> EmptyStateText(text = Strings.groupNoMatch, modifier = Modifier.fillMaxWidth())
                        else -> shown.forEach { friend: Friend ->
                            FriendPickRow(friend = friend, added = friend.userId in form.selectedIds, onToggle = { onToggleMember(friend.userId) })
                        }
                    }
                }
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.Sm)) {
            BasicText(text = form.error.orEmpty(), style = typography.micro.copy(color = colors.Error), modifier = Modifier.weight(1f))
            ProfileActionButton(text = Strings.groupCancel, kind = ProfileButtonKind.NEUTRAL, onClick = onClose)
            ProfileActionButton(text = Strings.groupCreate, kind = ProfileButtonKind.CONFIRM, onClick = onCreate, enabled = !form.creating, leadingIcon = Icons.Rounded.ChevronRight)
        }
    }
}

private const val SEARCH_MAX = 24
