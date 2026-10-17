package com.kglabs28.yapnplay.appengine.ui.components.lobby

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.ui.components.common.IconCircle
import com.kglabs28.yapnplay.appengine.ui.screens.playroomlobby.LobbyPrimaryButtonUi

/**
 * The lobby's main two-line button: START MATCH for the leader, READY /
 * NOT READY or WATCHING / NOT WATCHING for everyone else. Green when
 * active, dark when off, dimmed and inert when disabled.
 */
@Composable
fun LobbyPrimaryButton(
    button: LobbyPrimaryButtonUi,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val shape = YapNPlayTheme.shapes.pill
    val contentColor = if (button.active) colors.CtaGreenContent else colors.TextPrimary
    val icon = when {
        button.isStart -> Icons.Rounded.PlayArrow
        button.active -> Icons.Rounded.CheckCircle
        else -> Icons.Rounded.RadioButtonUnchecked
    }

    Row(
        modifier = modifier
            .graphicsLayer { alpha = if (button.enabled) 1f else DISABLED_ALPHA }
            .fillMaxHeight()
            .clip(shape)
            .background(
                if (button.active) {
                    Brush.horizontalGradient(listOf(colors.CtaGreenStart, colors.CtaGreenMid, colors.CtaGreenEnd))
                } else {
                    Brush.horizontalGradient(listOf(colors.RoomsSmallButton, colors.RoomsPanel))
                },
            )
            .border(dimens.hairline, colors.GlassShine.copy(alpha = 0.3f), shape)
            .clickable(enabled = button.enabled, role = Role.Button, onClick = onClick)
            .padding(horizontal = Spacing.Sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.Xs),
    ) {
        IconCircle(
            imageVector = icon,
            fill = colors.Background.copy(alpha = 0.35f),
            tint = contentColor,
            size = dimens.roomIcon,
            iconSize = dimens.iconMedium,
        )
        Column {
            BasicText(text = button.label, style = YapNPlayTheme.typography.button.copy(color = contentColor))
            BasicText(text = button.sublabel, style = YapNPlayTheme.typography.micro.copy(color = contentColor))
        }
    }
}

private const val DISABLED_ALPHA = 0.5f
