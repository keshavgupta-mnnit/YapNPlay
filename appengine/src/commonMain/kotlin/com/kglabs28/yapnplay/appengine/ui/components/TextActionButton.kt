package com.kglabs28.yapnplay.appengine.ui.components

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
import androidx.compose.ui.semantics.Role
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme

/**
 * Borderless text action (e.g. INVITE FRIENDS). Disabled ignores clicks.
 */
@Composable
fun TextActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val colors = YapNPlayTheme.colors
    Box(
        modifier = modifier
            .clickable(
                enabled = enabled,
                role = Role.Button,
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .defaultMinSize(minHeight = YapNPlayTheme.dimens.touchMin),
        contentAlignment = Alignment.Center,
    ) {
        BasicText(
            text = text,
            style = YapNPlayTheme.typography.label.copy(
                color = if (enabled) colors.TextSecondary else colors.TextDisabled,
            ),
            modifier = Modifier.padding(horizontal = Spacing.Md),
        )
    }
}
