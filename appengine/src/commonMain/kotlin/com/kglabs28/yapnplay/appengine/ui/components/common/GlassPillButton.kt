package com.kglabs28.yapnplay.appengine.ui.components.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.designsystem.glassPill

/**
 * The secondary pill of the design: see-through glass with a sheen and a
 * glowing rim in [accent] (Leaderboard, Rules, Change Game, Cancel, …).
 * Solid gradient call-to-actions stay [PillButton].
 */
@Composable
fun GlassPillButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    accent: Color = YapNPlayTheme.colors.SocialZoneAccent,
    contentColor: Color = YapNPlayTheme.colors.TextPrimary,
    enabled: Boolean = true,
    height: Dp = YapNPlayTheme.dimens.smallButtonHeight,
    iconSize: Dp = YapNPlayTheme.dimens.iconSmall,
    textStyle: TextStyle = YapNPlayTheme.typography.sectionTitle,
) {
    Row(
        modifier = modifier
            .graphicsLayer { alpha = if (enabled) 1f else DISABLED_ALPHA }
            .height(height)
            .glassPill(glowColor = accent, surface = YapNPlayTheme.colors.RoomsPanel)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .padding(horizontal = Spacing.Sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.Xs, Alignment.CenterHorizontally),
    ) {
        leadingIcon?.let { AppIcon(imageVector = it, tint = contentColor, size = iconSize) }
        // A label never takes the tap (it would swallow presses on the button); it only shrinks to fit.
        ShrinkText(text = text, style = textStyle.copy(color = contentColor), modifier = Modifier.weight(1f, fill = false), slideOnTap = false)
        trailingIcon?.let { AppIcon(imageVector = it, tint = contentColor, size = iconSize) }
    }
}

private const val DISABLED_ALPHA = 0.5f
