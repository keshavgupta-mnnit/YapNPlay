package com.kglabs28.yapnplay.appengine.ui.components.table

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.ui.components.common.AppIcon

/** Round avatar for the top bar strip; only its [ring] colour shows the voice state (no glow). */
@Composable
fun TableAvatar(
    ring: Color,
    modifier: Modifier = Modifier,
    size: Dp = YapNPlayTheme.dimens.ludoAvatar,
    ringWidth: Dp = YapNPlayTheme.dimens.ludoAvatarRing,
) {
    val colors = YapNPlayTheme.colors
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(colors.RoomsSmallButton)
            .border(ringWidth, ring, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        AppIcon(imageVector = Icons.Rounded.Person, tint = colors.IconMuted, size = size / 2)
    }
}
