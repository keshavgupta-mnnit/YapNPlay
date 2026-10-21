package com.kglabs28.yapnplay.appengine.ui.components.profile

import com.kglabs28.yapnplay.appengine.core.ui.ProfileStrings
import com.kglabs28.yapnplay.appengine.core.designsystem.AppBrushes
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.GroupAdd
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.ui.components.common.EmptyStateText
import com.kglabs28.yapnplay.appengine.ui.components.common.GlassPillButton
import com.kglabs28.yapnplay.appengine.ui.components.common.IconCircle
import com.kglabs28.yapnplay.appengine.ui.components.common.ModalCard
import com.kglabs28.yapnplay.appengine.ui.components.common.glassScrollbar
import com.kglabs28.yapnplay.appengine.ui.components.playrooms.badge
import com.kglabs28.yapnplay.appengine.ui.screens.profile.ProfileModal

/** "Invite <friend>": each of the user's groups with Invite (Invited once sent), then New Group with them. */
@Composable
fun InviteToGroupCard(
    modal: ProfileModal.InviteToGroup,
    onInvite: (roomId: String) -> Unit,
    onNewGroup: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val typography = YapNPlayTheme.typography
    ModalCard(
        surfaceBrush = AppBrushes.dialogGeneric,
        title = ProfileStrings.profileInviteTitle(modal.person.name),
        onClose = onClose,
        modifier = modifier,
    ) {
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(Spacing.Xs)) {
            BasicText(text = ProfileStrings.profileInviteHint, style = typography.micro.copy(color = colors.TextSecondary, textAlign = TextAlign.Center))
            modal.error?.let { BasicText(text = it, style = typography.micro.copy(color = colors.Error)) }
            if (!modal.loading && modal.groups.isEmpty()) {
                EmptyStateText(text = ProfileStrings.profileNoGroups, modifier = Modifier.fillMaxWidth())
            }
            if (modal.groups.isNotEmpty()) {
                val scroll = rememberScrollState()
                Column(
                    modifier = Modifier.fillMaxWidth().heightIn(max = dimens.groupListMaxHeight).glassScrollbar(scroll).verticalScroll(scroll).padding(end = dimens.scrollbarWidth * 2),
                    verticalArrangement = Arrangement.spacedBy(Spacing.Xs),
                ) {
                    modal.groups.forEach { group ->
                        val badge = group.icon.badge()
                        Row(
                            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(dimens.cbRowRadius)).background(colors.ListRow).padding(Spacing.Xs),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Spacing.Sm),
                        ) {
                            IconCircle(imageVector = badge.icon, fill = badge.fill, size = dimens.friendPickAvatar, iconSize = dimens.iconSmall)
                            Column(modifier = Modifier.weight(1f)) {
                                BasicText(text = group.name, style = typography.label.copy(color = colors.TextPrimary, fontWeight = FontWeight.Bold), maxLines = 1, overflow = TextOverflow.Ellipsis)
                                BasicText(text = group.detail, style = typography.micro.copy(color = colors.TextSecondary), maxLines = 1)
                            }
                            if (group.invited) {
                                GlassPillButton(text = ProfileStrings.profileInvited, onClick = {}, leadingIcon = Icons.Rounded.Check, accent = colors.LiveGreen, contentColor = colors.LiveGreen)
                            } else {
                                ProfileActionButton(
                                    text = ProfileStrings.profileInvite,
                                    kind = ProfileButtonKind.CONFIRM,
                                    onClick = { onInvite(group.roomId) },
                                    enabled = !group.sending,
                                    leadingIcon = Icons.Rounded.Add,
                                    height = dimens.smallButtonHeight,
                                )
                            }
                        }
                    }
                }
            }
            ProfileActionButton(text = ProfileStrings.profileNewGroup, kind = ProfileButtonKind.NEUTRAL, onClick = onNewGroup, leadingIcon = Icons.Rounded.GroupAdd, modifier = Modifier.fillMaxWidth())
        }
    }
}
