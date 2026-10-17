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
 * smaller ones (friend arrows, share) via [size]/[iconSize], and on/off
 * toggles (mic, speaker) by passing the icon and [tint]/[glowColor] for
 * the current state.
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
    tint: Color = YapNPlayTheme.colors.IconMuted,
    glowColor: Color = YapNPlayTheme.colors.IconMuted,
) {
    Box(
        modifier = modifier
            .size(size)
            .glassPill(glowColor = glowColor, surface = surface)
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        AppIcon(
            imageVector = imageVector,
            tint = tint,
            size = iconSize,
            contentDescription = contentDescription,
        )
    }
}
