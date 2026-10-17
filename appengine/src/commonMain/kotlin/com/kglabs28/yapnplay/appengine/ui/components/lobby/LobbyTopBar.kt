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
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.HowToReg
import androidx.compose.material.icons.rounded.Share
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.designsystem.glassPill
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.ui.components.common.GlassIconButton
import com.kglabs28.yapnplay.appengine.ui.components.common.IconCircle
import com.kglabs28.yapnplay.appengine.ui.components.common.PillButton

/**
 * Lobby top bar: back, room badge with name and tagline, then Waiting
 * List (admin only), Leaderboard and Share Link.
 */
@Composable
fun LobbyTopBar(
    roomName: String,
    description: String,
    showWaitingList: Boolean,
    onBack: () -> Unit,
    onWaitingList: () -> Unit,
    onLeaderboard: () -> Unit,
    onShareLink: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val typography = YapNPlayTheme.typography

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
                .padding(horizontal = Spacing.Xs),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.Xs),
        ) {
            IconCircle(imageVector = Icons.Rounded.Groups, fill = colors.SocialZoneIconFill, iconSize = dimens.iconMedium)
            Column {
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
        if (showWaitingList) {
            TopAction(Strings.lobbyWaitingList, Icons.Rounded.HowToReg, onWaitingList)
        }
        TopAction(Strings.lobbyLeaderboard, Icons.Rounded.EmojiEvents, onLeaderboard)
        TopAction(Strings.lobbyShareLink, Icons.Rounded.Share, onShareLink)
    }
}

@Composable
private fun TopAction(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    PillButton(
        text = label,
        onClick = onClick,
        gradient = listOf(colors.RoomsSmallButton, colors.RoomsPanel),
        contentColor = colors.TextPrimary,
        rimColor = colors.SocialZoneAccent,
        height = YapNPlayTheme.dimens.smallButtonHeight,
        leadingIcon = icon,
        trailingIcon = null,
        iconSize = YapNPlayTheme.dimens.iconSmall,
        textStyle = YapNPlayTheme.typography.sectionTitle,
        modifier = modifier,
    )
}
