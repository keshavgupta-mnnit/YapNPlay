package com.kglabs28.yapnplay.appengine.ui.components.games

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.designsystem.glassPill
import com.kglabs28.yapnplay.appengine.ui.components.common.AppIcon

/**
 * Tall glass ‹ / › button at a carousel edge.
 */
@Composable
fun CarouselArrowButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens

    Box(
        modifier = modifier
            .size(width = dimens.carouselArrowWidth, height = dimens.carouselArrowHeight)
            .glassPill(
                glowColor = colors.IconMuted,
                surface = colors.RoomsSmallButton,
                shape = RoundedCornerShape(dimens.cardRadius),
            )
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        AppIcon(
            imageVector = icon,
            tint = colors.TextPrimary,
            contentDescription = contentDescription,
        )
    }
}
