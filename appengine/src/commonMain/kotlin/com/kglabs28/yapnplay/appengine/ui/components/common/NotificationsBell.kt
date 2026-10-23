package com.kglabs28.yapnplay.appengine.ui.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.designsystem.glassPanel
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.ui.screens.main.NotificationUi

/**
 * Top bar bell: unread badge, and a glass drop-down of the last
 * notifications under it. Tapping a row opens that room.
 */
@Composable
fun NotificationsBell(
    notifications: List<NotificationUi>,
    unreadCount: Int,
    expanded: Boolean,
    onToggle: () -> Unit,
    onDismiss: () -> Unit,
    onNotificationClick: (roomId: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val dimens = YapNPlayTheme.dimens
    Box(modifier = modifier) {
        GlassIconButton(
            imageVector = Icons.Rounded.Notifications,
            contentDescription = Strings.topBarNotifications,
            onClick = onToggle,
            tint = if (expanded) YapNPlayTheme.colors.TextPrimary else YapNPlayTheme.colors.HomeTextSoft,
            surface = YapNPlayTheme.colors.TopBarButtonFrost,
        )
        if (unreadCount > 0) {
            UnreadBadge(count = unreadCount, modifier = Modifier.align(Alignment.TopEnd))
        }
        if (expanded) {
            // Hangs under the bell, right edges lined up.
            val gapPx = with(LocalDensity.current) { (dimens.topBarControlHeight + Spacing.Xs).roundToPx() }
            Popup(
                alignment = Alignment.TopEnd,
                offset = IntOffset(0, gapPx),
                onDismissRequest = onDismiss,
                properties = PopupProperties(focusable = true),
            ) {
                NotificationsList(notifications = notifications, onNotificationClick = onNotificationClick)
            }
        }
    }
}

@Composable
private fun UnreadBadge(count: Int, modifier: Modifier = Modifier) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    Box(
        modifier = modifier
            .sizeIn(minWidth = dimens.notificationBadge, minHeight = dimens.notificationBadge)
            .background(colors.Error, CircleShape)
            .padding(horizontal = Spacing.Xxs),
        contentAlignment = Alignment.Center,
    ) {
        BasicText(
            text = if (count > AppConstants.NOTIFICATION_BADGE_MAX) "${AppConstants.NOTIFICATION_BADGE_MAX}+" else "$count",
            style = YapNPlayTheme.typography.micro.copy(color = colors.TextPrimary),
        )
    }
}

@Composable
private fun NotificationsList(
    notifications: List<NotificationUi>,
    onNotificationClick: (roomId: String) -> Unit,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val typography = YapNPlayTheme.typography
    Column(
        modifier = Modifier
            .width(dimens.notificationsWidth)
            .glassPanel(glowColor = colors.SocialZoneAccent, surface = colors.RoomsPanel)
            .padding(vertical = Spacing.Sm),
    ) {
        BasicText(
            text = Strings.topBarNotifications,
            style = typography.sectionTitle.copy(color = colors.TextPrimary),
            modifier = Modifier.padding(horizontal = Spacing.Md, vertical = Spacing.Xs),
        )
        if (notifications.isEmpty()) {
            EmptyStateText(
                text = Strings.notificationsEmpty,
                modifier = Modifier.fillMaxWidth().padding(Spacing.Lg),
            )
            return@Column
        }
        Column(
            modifier = Modifier
                .heightIn(max = dimens.notificationsMaxHeight)
                .verticalScroll(rememberScrollState()),
        ) {
            notifications.forEachIndexed { index, item ->
                if (index > 0) {
                    Box(
                        Modifier
                            .padding(horizontal = Spacing.Md)
                            .fillMaxWidth()
                            .height(dimens.hairline)
                            .background(colors.TextSecondary.copy(alpha = 0.15f)),
                    )
                }
                NotificationRow(item = item, onClick = { onNotificationClick(item.roomId) })
            }
        }
    }
}

@Composable
private fun NotificationRow(item: NotificationUi, onClick: () -> Unit) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val typography = YapNPlayTheme.typography
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(if (item.unread) colors.SocialZoneAccent.copy(alpha = 0.08f) else colors.Transparent)
            .padding(horizontal = Spacing.Md, vertical = Spacing.Sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.Sm),
    ) {
        IconCircle(imageVector = Icons.Rounded.Groups, fill = colors.SocialZoneAccent)
        Column(modifier = Modifier.weight(1f)) {
            BasicText(
                text = item.title,
                style = typography.itemTitle.copy(color = colors.TextPrimary),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (item.body.isNotEmpty()) {
                BasicText(
                    text = item.body,
                    style = typography.caption.copy(color = colors.TextSecondary),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(Spacing.Xxs)) {
            BasicText(text = item.timeLabel, style = typography.micro.copy(color = colors.TextSecondary))
            if (item.unread) {
                Box(Modifier.size(dimens.statusDotSmall).background(colors.SocialZoneAccent, CircleShape))
            }
        }
    }
}
