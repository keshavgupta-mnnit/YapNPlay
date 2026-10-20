package com.kglabs28.yapnplay.appengine.ui.components.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Alignment
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Block
import androidx.compose.material.icons.rounded.GroupAdd
import androidx.compose.material.icons.rounded.PersonRemove
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.ui.components.common.ModalCard
import com.kglabs28.yapnplay.appengine.ui.screens.profile.ProfilePersonUi

/** "Friend Profile": their header, then a green Invite to Group, a red Remove Friend and a glass Block User. */
@Composable
fun FriendProfileCard(
    person: ProfilePersonUi,
    onCopyCode: () -> Unit,
    onRemove: () -> Unit,
    onBlock: () -> Unit,
    onInviteToGroup: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ModalCard(
        surfaceBrush = Brush.verticalGradient(listOf(YapNPlayTheme.colors.GlassDialogTop, YapNPlayTheme.colors.GlassDialogBottom)),title = Strings.profileFriendCardTitle, onClose = onClose, modifier = modifier) {
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(Spacing.Xs)) {
            PersonHeader(person = person, onCopyCode = onCopyCode)
            val buttonWidth = Modifier.width(YapNPlayTheme.dimens.profileCardButtonWidth)
            ProfileActionButton(text = Strings.profileInviteToGroup, kind = ProfileButtonKind.CONFIRM, onClick = onInviteToGroup, leadingIcon = Icons.Rounded.GroupAdd, modifier = buttonWidth)
            ProfileActionButton(text = Strings.profileRemoveFriend, kind = ProfileButtonKind.DANGER, onClick = onRemove, leadingIcon = Icons.Rounded.PersonRemove, modifier = buttonWidth)
            ProfileActionButton(text = Strings.profileBlockUser, kind = ProfileButtonKind.NEUTRAL, onClick = onBlock, leadingIcon = Icons.Rounded.Block, modifier = buttonWidth)
        }
    }
}
