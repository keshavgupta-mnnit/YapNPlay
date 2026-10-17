package com.kglabs28.yapnplay.appengine.core.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape

/**
 * Neon look from the member-list design (the green "Ready" pill): a
 * soft glow of [color] around the shape, a dark tint of it inside and a
 * bright rim. The ready chip and the ON switch share it.
 */
@Composable
fun Modifier.neonGlow(
    color: Color,
    shape: Shape = YapNPlayTheme.shapes.pill,
    fillAlpha: Float = 0.18f,
): Modifier {
    val dimens = YapNPlayTheme.dimens
    return this
        .shadow(dimens.neonGlow, shape, ambientColor = color, spotColor = color)
        .clip(shape)
        .background(YapNPlayTheme.colors.Background)
        .background(color.copy(alpha = fillAlpha))
        .border(dimens.borderThin, color, shape)
}
