package com.kglabs28.yapnplay.appengine.ui.components

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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import com.kglabs28.yapnplay.appengine.core.designsystem.headlineFontFamily
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme

/**
 * Secondary action. Ghost face with an accent border. Disabled ignores clicks.
 */
@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val border = if (enabled) colors.Cyan.copy(alpha = 0.6f) else colors.TextDisabled
    val content = if (enabled) colors.Cyan else colors.TextDisabled
    Box(
        modifier = modifier
            .clip(YapNPlayTheme.shapes.button)
            .border(dimens.borderThin, border, YapNPlayTheme.shapes.button)
            .clickable(
                enabled = enabled,
                role = Role.Button,
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .defaultMinSize(minHeight = dimens.buttonHeight),
        contentAlignment = Alignment.Center,
    ) {
        BasicText(
            text = text.uppercase(),
            style = YapNPlayTheme.typography.label.copy(
                fontFamily = headlineFontFamily(),
                fontWeight = FontWeight.SemiBold,
                color = content,
            ),
            modifier = Modifier.padding(horizontal = Spacing.Lg),
        )
    }
}
