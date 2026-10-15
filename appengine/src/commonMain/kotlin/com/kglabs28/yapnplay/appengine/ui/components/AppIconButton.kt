package com.kglabs28.yapnplay.appengine.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme

/**
 * Square icon button (settings, chat, fullscreen). The glyph is caller
 * content so icons stay swappable; keep it at least 48dp.
 */
@Composable
fun AppIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    accent: Color = YapNPlayTheme.colors.Cyan,
    content: @Composable () -> Unit,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    Box(
        modifier = modifier
            .size(dimens.touchMin)
            .clip(YapNPlayTheme.shapes.button)
            .background(if (enabled) colors.Panel else colors.Raised)
            .border(dimens.borderThin, accent.copy(alpha = 0.6f), YapNPlayTheme.shapes.button)
            .clickable(
                enabled = enabled,
                role = Role.Button,
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

/**
 * Text glyph fallback for icon buttons until art assets land.
 */
@Composable
fun AppIconGlyph(
    glyph: String,
    modifier: Modifier = Modifier,
    color: Color = YapNPlayTheme.colors.TextPrimary,
) {
    BasicText(
        text = glyph,
        style = YapNPlayTheme.typography.title.copy(color = color),
        modifier = modifier,
    )
}
