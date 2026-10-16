package com.kglabs28.yapnplay.appengine.core.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

/**
 * The glass look used on every bordered box in the app: glass surface,
 * illuminating gradient border, and a soft glow in the caller's color.
 * Pass the zone color — nothing else to configure.
 */
@Composable
fun Modifier.glassPanel(
    glowColor: Color,
    cornerRadius: Dp = YapNPlayTheme.dimens.cardRadius,
): Modifier {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val shape = RoundedCornerShape(cornerRadius)
    return this
        .shadow(dimens.glowBlur, shape, spotColor = glowColor.copy(alpha = 0.35f))
        .clip(shape)
        .background(colors.GlassSurface)
        .border(
            dimens.hairline,
            Brush.linearGradient(
                colors = listOf(
                    glowColor.copy(alpha = 0.9f),
                    glowColor.copy(alpha = 0.25f),
                    colors.TextDisabled.copy(alpha = 0.3f),
                ),
            ),
            shape,
        )
}
