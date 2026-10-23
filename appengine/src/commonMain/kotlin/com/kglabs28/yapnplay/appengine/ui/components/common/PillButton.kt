package com.kglabs28.yapnplay.appengine.ui.components.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.designsystem.glassPill

/**
 * The app's call-to-action pill (QUICK PLAY, CREATE ROOM, JOIN, OK…), in
 * the same see-through glass as every other button: dark glass, a sheen,
 * and a rim glowing in the button's colour — the brightest of [gradient]
 * (or [rimColor] when they're all dark). A dark [contentColor] (meant for
 * the old solid fill) reads in that colour instead, so it stays legible
 * on the glass. A null [leadingIcon] or [trailingIcon] is simply not
 * drawn. Disabled dims the button and ignores taps.
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
    val brightest = gradient.maxByOrNull { it.luminance() } ?: rimColor
    val glow = if (brightest.luminance() < DARK_GLOW) rimColor.copy(alpha = 1f) else brightest
    val content = if (contentColor.luminance() < DARK_TEXT) glow else contentColor

    Row(
        modifier = modifier
            // Disabled dimming on one layer, read at draw time, so enabled ↔ disabled always redraws.
            .graphicsLayer { alpha = if (enabled) 1f else DISABLED_ALPHA }
            .height(height)
            .glassPill(glowColor = glow, surface = YapNPlayTheme.colors.RoomsPanel)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .padding(horizontal = Spacing.Md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.Xs, Alignment.CenterHorizontally),
    ) {
        leadingIcon?.let {
            AppIcon(imageVector = it, tint = content, size = iconSize)
        }
        FitText(text = text, style = textStyle.copy(color = content), modifier = Modifier.weight(1f, fill = false))
        trailingIcon?.let {
            AppIcon(imageVector = it, tint = content, size = iconSize)
        }
    }
}

private const val DISABLED_ALPHA = 0.5f
/** Below this luminance a glow colour is too dark to read as a rim. */
private const val DARK_GLOW = 0.15f
/** Below this luminance text was meant for a bright fill and is lost on dark glass. */
private const val DARK_TEXT = 0.3f
