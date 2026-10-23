package com.kglabs28.yapnplay.appengine.ui.components.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.ui.components.common.IconCircle
import com.kglabs28.yapnplay.appengine.ui.screens.profile.ProfileStatUi

/**
 * Who the user is: the avatar (pencil on its corner: tap to change);
 * beside it the name at the start and the YapNPlay
 * ID at the end of a row exactly as wide as Player Stats below it.
 */
@Composable
fun ProfileIdentityCard(
    name: String,
    userCode: String,
    avatarId: String,
    stats: List<ProfileStatUi>,
    codeCopied: Boolean,
    onChangeAvatar: () -> Unit,
    onEditName: () -> Unit,
    onCopyCode: () -> Unit,
    modifier: Modifier = Modifier,
    onOpenHistory: (() -> Unit)? = null,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val typography = YapNPlayTheme.typography
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.Md)) {
        Box(modifier = Modifier.clickable(onClick = onChangeAvatar), contentAlignment = Alignment.BottomEnd) {
            PresetAvatar(avatarId = avatarId, size = dimens.profileAvatar, rimColor = colors.Cyan)
            IconCircle(imageVector = Icons.Rounded.Edit, fill = colors.SocialZoneAccent, size = dimens.profileEditBadge, iconSize = dimens.iconSmall)
        }
        Column(modifier = Modifier.width(IntrinsicSize.Max), verticalArrangement = Arrangement.spacedBy(Spacing.Xs)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.Lg)) {
                ProfileEditableText(text = name, style = typography.title.copy(color = colors.TextPrimary), onEdit = onEditName)
                Spacer(Modifier.weight(1f))
                ProfileIdPill(code = userCode, copied = codeCopied, onCopy = onCopyCode)
            }
            ProfileStatsPanel(stats = stats, modifier = Modifier.fillMaxWidth(), onOpenHistory = onOpenHistory)
        }
    }
}
