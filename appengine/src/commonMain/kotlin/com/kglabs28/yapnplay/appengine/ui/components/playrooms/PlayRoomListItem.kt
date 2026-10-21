package com.kglabs28.yapnplay.appengine.ui.components.playrooms

import com.kglabs28.yapnplay.appengine.core.ui.RoomStrings
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Casino
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.FamilyRestroom
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.SportsEsports
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.features.playrooms.domain.PlayRoom
import com.kglabs28.yapnplay.appengine.features.playrooms.domain.PlayRoomIcon
import com.kglabs28.yapnplay.appengine.ui.components.common.AppIcon
import com.kglabs28.yapnplay.appengine.ui.components.common.IconCircle

/**
 * One row in "Your Rooms": room badge, name (crown when you lead it),
 * member count, how many are live right now, and a chevron.
 */
@Composable
fun PlayRoomListItem(
    room: PlayRoom,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val typography = YapNPlayTheme.typography
    val shape = RoundedCornerShape(dimens.rowRadius)

    val (badgeIcon, badgeColor) = room.icon.badge()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(dimens.roomRowHeight)
            .clip(shape)
            .background(colors.RoomsRow.copy(alpha = 0.85f))
            .border(dimens.hairline, colors.GlassShine.copy(alpha = 0.08f), shape)
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.Xs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.Xs),
    ) {
        IconCircle(
            imageVector = badgeIcon,
            fill = badgeColor,
            size = dimens.roomIcon,
        )
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.Xxs),
            ) {
                BasicText(
                    text = room.name,
                    style = typography.itemTitle.copy(color = colors.TextPrimary),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false),
                )
                if (room.isLeader) {
                    AppIcon(
                        imageVector = Icons.Rounded.EmojiEvents,
                        tint = colors.Gold,
                        size = dimens.iconTiny,
                    )
                }
            }
            BasicText(
                text = RoomStrings.roomMembers(room.memberCount),
                style = typography.micro.copy(color = colors.TextSecondary),
                maxLines = 1,
            )
        }
        LiveBadge(count = room.liveCount)
        AppIcon(
            imageVector = Icons.Rounded.ChevronRight,
            tint = colors.IconMuted,
            size = dimens.iconSmall,
        )
    }
}
