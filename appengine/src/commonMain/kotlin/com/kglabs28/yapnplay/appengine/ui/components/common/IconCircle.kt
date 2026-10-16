package com.kglabs28.yapnplay.appengine.ui.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme

/**
 * Icon centered in a filled circle with a soft rim: card header badges,
 * room icons, banner icons and avatar placeholders.
 */
@Composable
fun IconCircle(
    imageVector: ImageVector,
    fill: Color,
    modifier: Modifier = Modifier,
    tint: Color = YapNPlayTheme.colors.TextPrimary,
    size: Dp = YapNPlayTheme.dimens.cardHeaderIconSize,
    iconSize: Dp = YapNPlayTheme.dimens.iconSmall,
    rimColor: Color = YapNPlayTheme.colors.GlassShine.copy(alpha = 0.2f),
) {
    val dimens = YapNPlayTheme.dimens
    val shape = YapNPlayTheme.shapes.avatar

    Box(
        modifier = modifier
            .size(size)
            .clip(shape)
            .background(fill)
            .border(dimens.hairline, rimColor, shape),
        contentAlignment = Alignment.Center,
    ) {
        AppIcon(imageVector = imageVector, tint = tint, size = iconSize)
    }
}
