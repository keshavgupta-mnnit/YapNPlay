package com.kglabs28.yapnplay.appengine.ui.components.profile

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
import androidx.compose.material.icons.rounded.Keyboard
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.designsystem.glassPanel
import com.kglabs28.yapnplay.appengine.core.designsystem.glassPill
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.ui.components.common.AppIcon
import com.kglabs28.yapnplay.appengine.ui.components.common.GlassIconButton

/**
 * In-app keyboard for a friend's ID, like the Rooms tab's number pad but a
 * full QWERTY with digits: keys an ID can't contain (0, 1, I, O) are
 * dimmed; backspace and Add Friend on the bottom row; a close cross on top.
 */
@Composable
fun FriendCodeKeyboard(
    onKey: (Char) -> Unit,
    onBackspace: () -> Unit,
    onAdd: () -> Unit,
    onClose: () -> Unit,
    addEnabled: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    Column(
        modifier = modifier
            .glassPanel(glowColor = colors.GameZoneAccent, surface = colors.RoomsPanel.copy(alpha = PANEL_ALPHA))
            .padding(Spacing.Xs),
        verticalArrangement = Arrangement.spacedBy(Spacing.Xxs),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.Xs)) {
            AppIcon(imageVector = Icons.Rounded.Keyboard, tint = colors.IconMuted, size = dimens.iconSmall)
            BasicText(text = Strings.profileKeyboardTitle, style = YapNPlayTheme.typography.label.copy(color = colors.TextPrimary), modifier = Modifier.weight(1f))
            GlassIconButton(
                imageVector = Icons.Rounded.Close,
                contentDescription = Strings.lobbyClose,
                onClick = onClose,
                size = dimens.smallButtonHeight,
                iconSize = dimens.iconSmall,
                tint = colors.TextPrimary,
            )
        }
        KEY_ROWS.forEachIndexed { index, row ->
            KeyRow {
                row.forEach { key -> LetterKey(key = key, onClick = { onKey(key) }) }
                if (index == KEY_ROWS.lastIndex) {
                    IconKey(Icons.AutoMirrored.Rounded.Backspace, Strings.profileKeyboardDelete, colors.IconMuted, onBackspace)
                    IconKey(
                        icon = Icons.Rounded.PersonAdd,
                        description = Strings.profileAddButton,
                        tint = if (addEnabled) colors.ReadyGreen else colors.TextDisabled,
                        onClick = onAdd,
                        enabled = addEnabled,
                        label = Strings.profileAddButton,
                        weight = ADD_KEY_WEIGHT,
                    )
                }
            }
        }
    }
}

@Composable
private fun ColumnScope.KeyRow(content: @Composable RowScope.() -> Unit) {
    Row(modifier = Modifier.weight(1f).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Spacing.Xxs), content = content)
}

@Composable
private fun RowScope.LetterKey(key: Char, onClick: () -> Unit) {
    val usable = key in AppConstants.USER_CODE_ALPHABET
    KeyTile(onClick = onClick, glow = YapNPlayTheme.colors.IconMuted, enabled = usable) {
        BasicText(text = key.toString(), style = YapNPlayTheme.typography.sectionTitle.copy(color = YapNPlayTheme.colors.TextPrimary))
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
    weight: Float = 1f,
) {
    KeyTile(onClick = onClick, glow = tint, enabled = enabled, weight = weight, modifier = Modifier.semantics { contentDescription = description }) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.Xxs)) {
            AppIcon(imageVector = icon, tint = tint, size = YapNPlayTheme.dimens.iconSmall)
            label?.let { BasicText(text = it, style = YapNPlayTheme.typography.micro.copy(color = tint), maxLines = 1) }
        }
    }
}

@Composable
private fun RowScope.KeyTile(
    onClick: () -> Unit,
    glow: Color,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    weight: Float = 1f,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .weight(weight)
            .fillMaxHeight()
            .graphicsLayer { alpha = if (enabled) 1f else DISABLED_ALPHA }
            .glassPill(glowColor = glow, surface = YapNPlayTheme.colors.RoomsSmallButton, shape = RoundedCornerShape(YapNPlayTheme.dimens.rowRadius))
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

private val KEY_ROWS = listOf("1234567890", "QWERTYUIOP", "ASDFGHJKL", "ZXCVBNM").map { it.toList() }
private const val PANEL_ALPHA = 0.92f
private const val DISABLED_ALPHA = 0.35f
private const val ADD_KEY_WEIGHT = 2f
