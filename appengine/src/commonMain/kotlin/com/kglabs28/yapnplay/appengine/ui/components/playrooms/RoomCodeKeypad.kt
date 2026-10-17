package com.kglabs28.yapnplay.appengine.ui.components.playrooms

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Backspace
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Dialpad
import androidx.compose.material.icons.automirrored.rounded.Login
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.designsystem.glassPanel
import com.kglabs28.yapnplay.appengine.core.designsystem.glassPill
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.ui.components.common.AppIcon
import com.kglabs28.yapnplay.appengine.ui.components.common.GlassIconButton

/**
 * In-app number pad for the room code, shown in the Rooms tab's right
 * column instead of the system keyboard: 1–9, then backspace, 0 and
 * JOIN, under a header with a close cross. Keys are glass tiles that
 * share the panel's height evenly.
 */
@Composable
fun RoomCodeKeypad(
    onDigit: (Char) -> Unit,
    onBackspace: () -> Unit,
    onJoin: () -> Unit,
    onClose: () -> Unit,
    joinEnabled: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens

    Column(
        modifier = modifier
            .glassPanel(glowColor = colors.GameZoneAccent, surface = colors.RoomsPanel.copy(alpha = 0.92f))
            .padding(Spacing.Sm),
        verticalArrangement = Arrangement.spacedBy(Spacing.Xs),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.Xs)) {
            AppIcon(imageVector = Icons.Rounded.Dialpad, tint = colors.IconMuted)
            BasicText(
                text = Strings.roomsKeypadTitle,
                style = YapNPlayTheme.typography.sectionTitle.copy(color = colors.TextPrimary),
                modifier = Modifier.weight(1f),
            )
            GlassIconButton(
                imageVector = Icons.Rounded.Close,
                contentDescription = Strings.lobbyClose,
                onClick = onClose,
                size = dimens.smallButtonHeight,
                iconSize = dimens.iconSmall,
                tint = colors.TextPrimary,
            )
        }
        KEY_ROWS.forEach { row ->
            KeyRow {
                row.forEach { digit -> DigitKey(digit = digit, onClick = { onDigit(digit) }) }
            }
        }
        KeyRow {
            IconKey(Icons.AutoMirrored.Rounded.Backspace, Strings.roomsKeypadBackspace, colors.IconMuted, onBackspace)
            DigitKey(digit = '0', onClick = { onDigit('0') })
            IconKey(
                icon = Icons.AutoMirrored.Rounded.Login,
                description = Strings.roomsJoinButton,
                tint = if (joinEnabled) colors.ReadyGreen else colors.TextDisabled,
                onClick = onJoin,
                enabled = joinEnabled,
                label = Strings.roomsJoinButton,
            )
        }
    }
}

@Composable
private fun ColumnScope.KeyRow(content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier
            .weight(1f)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.Xs),
        content = content,
    )
}

@Composable
private fun RowScope.DigitKey(digit: Char, onClick: () -> Unit) {
    KeyTile(onClick = onClick, glow = YapNPlayTheme.colors.IconMuted) {
        BasicText(
            text = digit.toString(),
            style = YapNPlayTheme.typography.title.copy(color = YapNPlayTheme.colors.TextPrimary),
        )
    }
}

@Composable
private fun RowScope.IconKey(
    icon: ImageVector,
    description: String,
    tint: Color,
    onClick: () -> Unit,
    enabled: Boolean = true,
    label: String? = null,
) {
    KeyTile(onClick = onClick, glow = tint, enabled = enabled, modifier = Modifier.semantics { contentDescription = description }) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.Xxs)) {
            AppIcon(imageVector = icon, tint = tint)
            label?.let { BasicText(text = it, style = YapNPlayTheme.typography.sectionTitle.copy(color = tint)) }
        }
    }
}

@Composable
private fun RowScope.KeyTile(
    onClick: () -> Unit,
    glow: Color,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .weight(1f)
            .fillMaxHeight()
            .glassPill(
                glowColor = glow,
                surface = YapNPlayTheme.colors.RoomsSmallButton,
                shape = RoundedCornerShape(YapNPlayTheme.dimens.rowRadius),
            )
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

private val KEY_ROWS = listOf("123", "456", "789").map { it.toList() }
