package com.kglabs28.yapnplay.appengine.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme

/**
 * Pill chip for a member or tag. 32dp tall, truncates long text.
 */
@Composable
fun MemberChip(
    text: String,
    modifier: Modifier = Modifier,
    accent: Color = YapNPlayTheme.colors.Cyan,
    onClick: (() -> Unit)? = null,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val clickableModifier = if (onClick != null) {
        Modifier.clickable(
            role = Role.Button,
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick,
        )
    } else {
        Modifier
    }
    Box(
        modifier = modifier
            .clip(YapNPlayTheme.shapes.pill)
            .background(colors.Panel)
            .border(dimens.borderThin, accent.copy(alpha = 0.6f), YapNPlayTheme.shapes.pill)
            .then(clickableModifier)
            .defaultMinSize(minHeight = dimens.chipHeight),
        contentAlignment = Alignment.Center,
    ) {
        BasicText(
            text = text,
            style = YapNPlayTheme.typography.label.copy(color = colors.TextPrimary),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = Spacing.Md),
        )
    }
}
