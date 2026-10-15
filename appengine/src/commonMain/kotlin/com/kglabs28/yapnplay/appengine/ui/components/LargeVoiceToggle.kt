package com.kglabs28.yapnplay.appengine.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme

/**
 * Large mic/speaker toggle for the lobby. Bigger than [ToggleIconButton]
 * so it is easy to hit mid-game. Defaults: mic off, speaker on.
 */
@Composable
fun LargeVoiceToggle(
    selected: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    size: Dp? = null,
    onAccent: Color = YapNPlayTheme.colors.Cyan,
    content: @Composable () -> Unit,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val resolvedSize = size ?: dimens.voiceToggle
    val border = when {
        !enabled -> colors.TextDisabled
        selected -> onAccent
        else -> colors.TextDisabled.copy(alpha = 0.6f)
    }
    Box(
        modifier = modifier
            .size(resolvedSize)
            .clip(YapNPlayTheme.shapes.button)
            .background(if (enabled) colors.Panel else colors.Raised)
            .border(dimens.borderThin, border, YapNPlayTheme.shapes.button)
            .semantics { this.selected = selected }
            .clickable(
                enabled = enabled,
                role = Role.Switch,
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { onToggle(!selected) },
            ),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}
