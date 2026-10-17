package com.kglabs28.yapnplay.appengine.ui.components.lobby

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.WifiOff
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.ui.components.common.AppIcon
import com.kglabs28.yapnplay.appengine.ui.screens.playroomlobby.LobbyMemberStatus

/**
 * Member status chip: Ready (green), Not Ready, Watching or Offline.
 * Nothing is drawn for [LobbyMemberStatus.NONE] (no game picked yet).
 */
@Composable
fun LobbyStatusChip(
    status: LobbyMemberStatus,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val shape = YapNPlayTheme.shapes.pill
    val (icon, label, color) = when (status) {
        LobbyMemberStatus.READY -> Triple(Icons.Rounded.CheckCircle, Strings.lobbyReady, colors.LiveGreen)
        LobbyMemberStatus.NOT_READY -> Triple(Icons.Rounded.RadioButtonUnchecked, Strings.lobbyNotReady, colors.TextSecondary)
        LobbyMemberStatus.WATCHING -> Triple(Icons.Rounded.Visibility, Strings.lobbyWatching, colors.Cyan)
        LobbyMemberStatus.OFFLINE -> Triple(Icons.Rounded.WifiOff, Strings.lobbyOffline, colors.TextDisabled)
        LobbyMemberStatus.NONE -> return
    }

    Row(
        modifier = modifier
            .clip(shape)
            .background(color.copy(alpha = 0.12f))
            .border(dimens.hairline, color.copy(alpha = 0.6f), shape)
            .padding(horizontal = Spacing.Xs, vertical = Spacing.Xxxs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.Xxs),
    ) {
        AppIcon(imageVector = icon, tint = color, size = dimens.iconTiny)
        BasicText(text = label, style = YapNPlayTheme.typography.micro.copy(color = color), maxLines = 1)
    }
}
