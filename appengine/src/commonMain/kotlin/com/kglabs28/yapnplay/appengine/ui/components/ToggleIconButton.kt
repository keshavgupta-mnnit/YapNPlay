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
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme

/**
 * On/off icon toggle (in-house rule switches, mic, speaker).
 * The accent colour marks the ON state.
 */
@Composable
fun ToggleIconButton(
    selected: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onAccent: Color = YapNPlayTheme.colors.Cyan,
    content: @Composable () -> Unit,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val border = when {
        !enabled -> colors.TextDisabled
        selected -> onAccent
        else -> colors.TextDisabled.copy(alpha = 0.6f)
    }
    Box(
        modifier = modifier
            .size(dimens.touchMin)
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
