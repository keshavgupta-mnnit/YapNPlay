package com.kglabs28.yapnplay.appengine.ui.components.groups

import com.kglabs28.yapnplay.appengine.ui.components.common.ShrinkText
import com.kglabs28.yapnplay.appengine.core.ui.RoomStrings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.features.profile.domain.Friend
import com.kglabs28.yapnplay.appengine.ui.components.PresenceDot
import com.kglabs28.yapnplay.appengine.ui.components.common.GlassPillButton
import com.kglabs28.yapnplay.appengine.ui.components.profile.PresetAvatar
import com.kglabs28.yapnplay.appengine.ui.components.profile.ProfileActionButton
import com.kglabs28.yapnplay.appengine.ui.components.profile.ProfileButtonKind

/** A friend to pick: avatar with online dot, name, then a green + ADD (or ADDED, which takes them off). */
@Composable
fun FriendPickRow(friend: Friend, added: Boolean, onToggle: () -> Unit, modifier: Modifier = Modifier) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(dimens.cbRowRadius))
            .background(colors.ListRow)
            .padding(horizontal = Spacing.Xs, vertical = Spacing.Xxs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.Xs),
    ) {
        Box(contentAlignment = Alignment.BottomEnd) {
            PresetAvatar(avatarId = friend.avatarId, size = dimens.friendPickAvatar)
            PresenceDot(online = friend.online, size = dimens.statusDotSmall, onlineColor = colors.LiveGreen)
        }
        ShrinkText(text = friend.name, style = YapNPlayTheme.typography.label.copy(color = colors.TextPrimary), modifier = Modifier.weight(1f))
        if (friend.online) Box(Modifier.size(dimens.statusDotSmall).background(colors.LiveGreen, CircleShape))
        if (added) {
            GlassPillButton(text = RoomStrings.groupAdded, onClick = onToggle, leadingIcon = Icons.Rounded.Check, accent = colors.LiveGreen, contentColor = colors.LiveGreen)
        } else {
            ProfileActionButton(text = RoomStrings.groupAdd, kind = ProfileButtonKind.CONFIRM, onClick = onToggle, leadingIcon = Icons.Rounded.Add, height = dimens.smallButtonHeight)
        }
    }
}
