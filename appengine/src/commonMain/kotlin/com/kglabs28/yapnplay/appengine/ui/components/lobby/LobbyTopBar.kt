package com.kglabs28.yapnplay.appengine.ui.components.lobby

import com.kglabs28.yapnplay.appengine.ui.components.common.ShrinkText
import com.kglabs28.yapnplay.appengine.core.ui.RoomStrings
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.HowToReg
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.material.icons.rounded.Share
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.designsystem.glassPanel
import com.kglabs28.yapnplay.appengine.core.designsystem.glassPill
import com.kglabs28.yapnplay.appengine.ui.components.common.AppIcon
import com.kglabs28.yapnplay.appengine.ui.components.common.GlassIconButton
import com.kglabs28.yapnplay.appengine.ui.components.common.GlassPillButton

/**
 * Lobby top bar: round back, the group's name at the start of its pill, then
 * Leaderboard and a round ⋮ that opens [LobbyRoomMenu] (everything else
 * the room offers lives there).
 */
@Composable
fun LobbyTopBar(
    roomName: String,
    menuOpen: Boolean,
    onBack: () -> Unit,
    onLeaderboard: () -> Unit,
    onMenu: () -> Unit,
    modifier: Modifier = Modifier,
    menu: @Composable () -> Unit = {},
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
            contentDescription = RoomStrings.lobbyBack,
            onClick = onBack,
            size = dimens.lobbyBarHeight,
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .glassPill(glowColor = colors.IconMuted, surface = colors.RoomsPanel)
                .padding(horizontal = Spacing.Md),
            contentAlignment = Alignment.CenterStart,
        ) {
            ShrinkText(
                text = roomName,
                style = typography.cardTitle.copy(color = colors.TextPrimary),
            )
        }
        // As tall as the room name's pill beside it.
        GlassPillButton(text = RoomStrings.lobbyLeaderboard, onClick = onLeaderboard, leadingIcon = Icons.Rounded.EmojiEvents, height = dimens.lobbyBarHeight)
        Box {
            GlassIconButton(
                imageVector = Icons.Rounded.MoreVert,
                contentDescription = RoomStrings.lobbyMore,
                onClick = onMenu,
                size = dimens.lobbyBarHeight,
            )
            if (menuOpen) menu()
        }
    }
}

/**
 * The ⋮ menu under the top bar's last button: Waiting List (admins),
 * Share Link, Add Friends, Edit Name (admins) and, last, Leave Room in red.
 */
@Composable
fun LobbyRoomMenu(
    isAdmin: Boolean,
    onDismiss: () -> Unit,
    onWaitingList: () -> Unit,
    onShareLink: () -> Unit,
    onAddFriends: () -> Unit,
    onEditName: () -> Unit,
    onLeave: () -> Unit,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    Popup(
        alignment = Alignment.TopEnd,
        offset = IntOffset(0, 0),
        onDismissRequest = onDismiss,
        properties = PopupProperties(focusable = true),
    ) {
        Column(
            modifier = Modifier
                .padding(top = dimens.lobbyBarHeight + Spacing.Xs)
                .width(dimens.lobbyMenuWidth)
                .glassPanel(glowColor = colors.IconMuted, surface = colors.RoomsPanel)
                .padding(vertical = Spacing.Xs),
        ) {
            if (isAdmin) LobbyMenuItem(Icons.Rounded.HowToReg, RoomStrings.lobbyWaitingList, colors.TextPrimary) { onDismiss(); onWaitingList() }
            LobbyMenuItem(Icons.Rounded.Share, RoomStrings.lobbyShareLink, colors.TextPrimary) { onDismiss(); onShareLink() }
            LobbyMenuItem(Icons.Rounded.PersonAdd, RoomStrings.lobbyAddFriends, colors.TextPrimary) { onDismiss(); onAddFriends() }
            if (isAdmin) LobbyMenuItem(Icons.Rounded.Edit, RoomStrings.lobbyEditName, colors.TextPrimary) { onDismiss(); onEditName() }
            LobbyMenuItem(Icons.AutoMirrored.Rounded.Logout, RoomStrings.lobbyLeaveRoom, colors.Error) { onDismiss(); onLeave() }
        }
    }
}

@Composable
private fun LobbyMenuItem(icon: ImageVector, text: String, tint: Color, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = Spacing.Md, vertical = Spacing.Sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.Sm),
    ) {
        AppIcon(imageVector = icon, tint = tint, size = YapNPlayTheme.dimens.iconSmall)
        BasicText(text = text, style = YapNPlayTheme.typography.label.copy(color = tint), maxLines = 1)
    }
}
