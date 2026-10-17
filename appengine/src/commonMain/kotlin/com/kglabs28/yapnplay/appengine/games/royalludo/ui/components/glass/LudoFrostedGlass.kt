package com.kglabs28.yapnplay.appengine.games.royalludo.ui.components.glass

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme

/**
 * Thick frosted-white glass from the Royal Ludo design: a soft glow of
 * [tint] around it, a pale milky body that darkens toward the bottom, a
 * bright band along the top edge (light catching the glass) and a rim
 * that is white on top and [tint] at the bottom.
 */
@Composable
fun Modifier.ludoFrostedGlass(tint: Color, shape: Shape, radius: Dp, glow: Dp = YapNPlayTheme.dimens.ludoCardGlow): Modifier {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    return this
        .drawBehind {
            val spread = glow.toPx()
            drawRoundRect(
                brush = Brush.radialGradient(
                    listOf(tint.copy(alpha = GLOW_ALPHA), Color.Transparent),
                    center = center,
                    radius = size.maxDimension / 2 + spread,
                ),
                topLeft = Offset(-spread, -spread),
                size = Size(size.width + spread * 2, size.height + spread * 2),
                cornerRadius = CornerRadius(radius.toPx() + spread),
            )
        }
        .clip(shape)
        .background(Brush.verticalGradient(listOf(colors.LudoFrostTop, colors.LudoFrostMid, colors.LudoFrostBottom)))
        .background(Brush.verticalGradient(listOf(tint.copy(alpha = TINT_TOP), tint.copy(alpha = TINT_BOTTOM))))
        .drawWithContent {
            drawContent()
            drawRect(
                brush = Brush.verticalGradient(listOf(Color.White.copy(alpha = SHINE_ALPHA), Color.Transparent), 0f, size.height * SHINE_DEPTH),
                size = Size(size.width, size.height * SHINE_DEPTH),
            )
        }
        .border(
            dimens.hairline,
            Brush.verticalGradient(listOf(colors.LudoFrostRim, tint.copy(alpha = RIM_TINT_ALPHA))),
            shape,
        )
}

private const val GLOW_ALPHA = 0.35f
private const val TINT_TOP = 0.04f
private const val TINT_BOTTOM = 0.16f
private const val SHINE_ALPHA = 0.22f
private const val SHINE_DEPTH = 0.35f
private const val RIM_TINT_ALPHA = 0.8f
