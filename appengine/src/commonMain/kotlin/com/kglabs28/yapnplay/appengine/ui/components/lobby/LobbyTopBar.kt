package com.kglabs28.yapnplay.appengine.ui.components.lobby

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.HowToReg
import androidx.compose.material.icons.rounded.Share
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.designsystem.glassPill
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.features.playrooms.domain.PlayRoomIcon
import com.kglabs28.yapnplay.appengine.ui.components.common.GlassIconButton
import com.kglabs28.yapnplay.appengine.ui.components.common.GlassPillButton
import com.kglabs28.yapnplay.appengine.ui.components.common.IconCircle
import com.kglabs28.yapnplay.appengine.ui.components.playrooms.badge

/**
 * Lobby top bar: round back, the room title pill (the room's own badge,
 * name, tagline), a round red Leave right after it, then Waiting List
 * (admin only), Leaderboard and Share Link as glass pills.
 */
@Composable
fun LobbyTopBar(
    roomName: String,
    description: String,
    roomIcon: PlayRoomIcon,
    showWaitingList: Boolean,
    onBack: () -> Unit,
    onShareLink: () -> Unit,
    onWaitingList: () -> Unit,
    onLeaderboard: () -> Unit,
    onLeave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val typography = YapNPlayTheme.typography
    val badge = roomIcon.badge()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(dimens.lobbyBarHeight),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.Xs),
    ) {
        GlassIconButton(
            imageVector = Icons.Rounded.ChevronLeft,
            contentDescription = Strings.lobbyBack,
            onClick = onBack,
            size = dimens.lobbyBarHeight,
        )
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .glassPill(glowColor = colors.IconMuted, surface = colors.RoomsPanel)
                .padding(start = Spacing.Xs, end = Spacing.Sm),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.Xs),
        ) {
            IconCircle(imageVector = badge.icon, fill = badge.fill, iconSize = dimens.iconMedium)
            Column(modifier = Modifier.weight(1f)) {
                BasicText(
                    text = roomName,
                    style = typography.cardTitle.copy(color = colors.TextPrimary),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                BasicText(
                    text = description,
                    style = typography.micro.copy(color = colors.RoomsSubtitle),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        GlassIconButton(
            imageVector = Icons.AutoMirrored.Rounded.Logout,
            contentDescription = Strings.lobbyLeaveRoom,
            onClick = onLeave,
            size = dimens.lobbyBarHeight,
            tint = colors.Error,
            glowColor = colors.Error,
        )
        if (showWaitingList) {
            GlassPillButton(text = Strings.lobbyWaitingList, onClick = onWaitingList, leadingIcon = Icons.Rounded.HowToReg)
        }
        GlassPillButton(text = Strings.lobbyLeaderboard, onClick = onLeaderboard, leadingIcon = Icons.Rounded.EmojiEvents)
        GlassPillButton(text = Strings.lobbyShareLink, onClick = onShareLink, leadingIcon = Icons.Rounded.Share)
    }
}
