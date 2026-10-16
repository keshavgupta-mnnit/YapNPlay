package com.kglabs28.yapnplay.appengine.ui.components.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.designsystem.glassPill

/**
 * Round glass icon button: top bar bell/settings at the default size,
 * smaller ones (friend-list arrows) by passing [size] and [iconSize].
 */
@Composable
fun GlassIconButton(
    imageVector: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = YapNPlayTheme.dimens.topBarControlHeight,
    iconSize: Dp = YapNPlayTheme.dimens.iconMedium,
    surface: Color = YapNPlayTheme.colors.TopBarButton,
) {
    val colors = YapNPlayTheme.colors

    Box(
        modifier = modifier
            .size(size)
            .glassPill(glowColor = colors.IconMuted, surface = surface)
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        AppIcon(
            imageVector = imageVector,
            tint = colors.IconMuted,
            size = iconSize,
            contentDescription = contentDescription,
        )
    }
}
