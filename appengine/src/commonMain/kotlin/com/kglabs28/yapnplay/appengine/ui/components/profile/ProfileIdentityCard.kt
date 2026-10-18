package com.kglabs28.yapnplay.appengine.ui.components.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.designsystem.glassPanel
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.ui.components.common.AppIcon
import com.kglabs28.yapnplay.appengine.ui.components.common.IconCircle
import com.kglabs28.yapnplay.appengine.ui.screens.profile.ProfileStatUi

/**
 * Who the user is: their avatar with a pencil badge (tap to change), name
 * and @username (tap to edit), their ID (tap to copy; "Copied" flashes),
 * the stats row, and Log out at the bottom once sign-in exists.
 */
@Composable
fun ProfileIdentityCard(
    name: String,
    username: String,
    userCode: String,
    avatarId: String,
    stats: List<ProfileStatUi>,
    codeCopied: Boolean,
    canLogOut: Boolean,
    onEditAvatar: () -> Unit,
    onEditName: () -> Unit,
    onEditUsername: () -> Unit,
    onCopyCode: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val typography = YapNPlayTheme.typography
    Column(
        modifier = modifier
            .glassPanel(glowColor = colors.SocialZoneAccent, surface = colors.RoomsPanel.copy(alpha = PANEL_ALPHA))
            .padding(Spacing.Sm),
        verticalArrangement = Arrangement.spacedBy(Spacing.Sm),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.Sm)) {
            Box(modifier = Modifier.clickable(onClick = onEditAvatar), contentAlignment = Alignment.BottomEnd) {
                PresetAvatar(avatarId = avatarId, size = dimens.profileAvatar, rimColor = colors.SocialZoneAccent)
                IconCircle(
                    imageVector = Icons.Rounded.Edit,
                    fill = colors.SocialZoneAccent,
                    size = dimens.profileEditBadge,
                    iconSize = dimens.iconSmall,
                )
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(Spacing.Xxxs)) {
                ProfileEditableText(text = name, style = typography.title.copy(color = colors.TextPrimary), onEdit = onEditName)
                ProfileEditableText(text = username, style = typography.label.copy(color = colors.TextSecondary), onEdit = onEditUsername)
                Row(
                    modifier = Modifier.clickable(onClick = onCopyCode),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.Xs),
                ) {
                    BasicText(text = Strings.profileUserCode(userCode), style = typography.label.copy(color = colors.Cyan, fontWeight = FontWeight.Bold))
                    AppIcon(imageVector = Icons.Rounded.ContentCopy, tint = colors.IconMuted, size = dimens.iconSmall, contentDescription = Strings.profileCopyCode)
                    if (codeCopied) BasicText(text = Strings.profileCopied, style = typography.micro.copy(color = colors.LiveGreen))
                }
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Spacing.Xs)) {
            stats.forEach { ProfileStatTile(stat = it, modifier = Modifier.weight(1f)) }
        }
        Spacer(Modifier.weight(1f))
        if (canLogOut) {
            TintedGlassButton(text = Strings.profileLogout, tint = colors.Error, onClick = onLogout, modifier = Modifier.fillMaxWidth())
        }
    }
}

private const val PANEL_ALPHA = 0.9f
