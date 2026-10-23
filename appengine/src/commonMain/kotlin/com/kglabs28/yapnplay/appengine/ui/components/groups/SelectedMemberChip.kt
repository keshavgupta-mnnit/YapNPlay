package com.kglabs28.yapnplay.appengine.ui.components.groups

import com.kglabs28.yapnplay.appengine.ui.components.common.ShrinkText
import com.kglabs28.yapnplay.appengine.core.ui.RoomStrings
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.designsystem.glassPill
import com.kglabs28.yapnplay.appengine.ui.components.common.AppIcon
import com.kglabs28.yapnplay.appengine.ui.components.profile.PresetAvatar

/** A picked member: avatar and name; the host shows "Host"; others a red × to remove them. */
@Composable
fun SelectedMemberChip(name: String, avatarId: String, isHost: Boolean, onRemove: () -> Unit, modifier: Modifier = Modifier) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val typography = YapNPlayTheme.typography
    Row(
        modifier = modifier
            .glassPill(glowColor = colors.IconMuted, surface = colors.RoomsSmallButton)
            .padding(start = Spacing.Xxs, end = Spacing.Xs, top = Spacing.Xxxs, bottom = Spacing.Xxxs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.Xs),
    ) {
        PresetAvatar(avatarId = avatarId, size = dimens.memberChipAvatar)
        ShrinkText(text = name, style = typography.label.copy(color = colors.TextPrimary))
        if (isHost) {
            BasicText(
                text = RoomStrings.groupHost,
                style = typography.micro.copy(color = colors.TextPrimary),
                modifier = Modifier.clip(YapNPlayTheme.shapes.pill).background(colors.RoomsInput).padding(horizontal = Spacing.Xs),
            )
        } else {
            Box(
                modifier = Modifier.size(dimens.fieldClearButton).clip(CircleShape).background(colors.Error).clickable(onClick = onRemove),
                contentAlignment = Alignment.Center,
            ) {
                AppIcon(imageVector = Icons.Rounded.Close, tint = colors.TextPrimary, size = dimens.iconTiny, contentDescription = RoomStrings.groupRemoveMember(name))
            }
        }
    }
}
