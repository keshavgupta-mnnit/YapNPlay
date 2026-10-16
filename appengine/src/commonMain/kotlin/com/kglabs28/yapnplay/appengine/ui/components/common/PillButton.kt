package com.kglabs28.yapnplay.appengine.ui.components.common

import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme

/**
 * Glowing gradient pill: QUICK PLAY, CREATE NEW ROOM, JOIN, Create Room.
 * Colors, height and icons are parameters, so every call-to-action in
 * the app is this one component. A null [leadingIcon] or [trailingIcon]
 * is simply not drawn. Disabled dims the button and ignores taps.
 */
@Composable
fun PillButton(
    text: String,
    onClick: () -> Unit,
    gradient: List<Color>,
    contentColor: Color,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    height: Dp = YapNPlayTheme.dimens.ctaHeight,
    rimColor: Color = YapNPlayTheme.colors.GlassShine.copy(alpha = 0.35f),
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = Icons.Rounded.ChevronRight,
    iconSize: Dp = YapNPlayTheme.dimens.iconMedium,
    textStyle: TextStyle = YapNPlayTheme.typography.button,
) {
    val dimens = YapNPlayTheme.dimens
    val shape = YapNPlayTheme.shapes.pill
    val glow = gradient.first()

    Row(
        modifier = modifier
            .height(height)
            .shadow(dimens.glowBlur, shape, ambientColor = glow, spotColor = glow)
            .clip(shape)
            .background(Brush.horizontalGradient(gradient))
            .border(dimens.hairline, rimColor, shape)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .alpha(if (enabled) 1f else 0.5f)
            .padding(horizontal = Spacing.Md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.Xs, Alignment.CenterHorizontally),
    ) {
        leadingIcon?.let {
            AppIcon(imageVector = it, tint = contentColor, size = iconSize)
        }
        BasicText(
            text = text,
            style = textStyle.copy(color = contentColor),
            maxLines = 1,
        )
        trailingIcon?.let {
            AppIcon(imageVector = it, tint = contentColor, size = iconSize)
        }
    }
}
