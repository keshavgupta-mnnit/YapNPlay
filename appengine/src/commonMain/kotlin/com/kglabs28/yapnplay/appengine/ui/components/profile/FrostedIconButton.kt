package com.kglabs28.yapnplay.appengine.ui.components.profile

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.ui.components.common.AppIcon
import com.kglabs28.yapnplay.appengine.ui.components.table.tableFrostedGlass

/** A round frosted glass button with an icon: white by default (the profile's Settings), or [tint]ed (its red Exit). */
@Composable
fun FrostedIconButton(
    icon: ImageVector,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = YapNPlayTheme.colors.AvatarRingSilver,
    size: Dp = YapNPlayTheme.dimens.topBarControlHeight,
    iconSize: Dp = YapNPlayTheme.dimens.iconMedium,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    Box(
        modifier = modifier
            .size(size)
            .tableFrostedGlass(tint = tint, shape = CircleShape, radius = size / 2, glow = dimens.zero)
            .clickable(role = Role.Button, onClick = onClick)
            .semantics { contentDescription = description },
        contentAlignment = Alignment.Center,
    ) {
        AppIcon(imageVector = icon, tint = colors.TextPrimary, size = iconSize)
    }
}
