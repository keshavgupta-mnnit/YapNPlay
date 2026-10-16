package com.kglabs28.yapnplay.appengine.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kglabs28.yapnplay.appengine.core.designsystem.HeadlineFontFamily
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme

/**
 * Primary call-to-action. Bright face, dark label in capitals,
 * glow on the active element only. Disabled ignores clicks.
 */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    containerColor: Color? = null,
    contentColor: Color? = null,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val background = containerColor ?: if (enabled) colors.ButtonFace else colors.Raised
    val content = contentColor ?: if (enabled) colors.Background else colors.TextDisabled
    Box(
        modifier = modifier
            .shadow(
                elevation = if (enabled) dimens.glowBlur else dimens.zero,
                shape = YapNPlayTheme.shapes.button,
                ambientColor = colors.Cyan.copy(alpha = 0.4f),
                spotColor = colors.Cyan.copy(alpha = 0.4f),
            )
            .clip(YapNPlayTheme.shapes.button)
            .background(background)
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
                fontFamily = HeadlineFontFamily,
                fontWeight = FontWeight.SemiBold,
                color = content,
            ),
            modifier = Modifier.padding(horizontal = Spacing.Lg),
        )
    }
}
