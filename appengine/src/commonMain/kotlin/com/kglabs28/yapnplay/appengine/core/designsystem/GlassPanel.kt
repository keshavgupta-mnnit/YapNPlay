package com.kglabs28.yapnplay.appengine.core.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.Dp

/**
 * The glass look used on every bordered box in the app: glass surface,
 * illuminating gradient border, and a soft glow in the caller's color.
 * Pass the zone color — nothing else to configure. [shape] and
 * [surface] let small controls (pills, round buttons) reuse the look;
 * [surfaceBrush] gives a card a gradient fill instead of a flat one.
 */
@Composable
fun Modifier.glassPanel(
    glowColor: Color,
    cornerRadius: Dp = YapNPlayTheme.dimens.cardRadius,
    shape: Shape = RoundedCornerShape(cornerRadius),
    surface: Color = YapNPlayTheme.colors.GlassSurface,
    surfaceBrush: Brush = SolidColor(surface),
    /** A rim of its own (e.g. a neon gradient) instead of the lit-from-above one; null keeps that. */
    rimBrush: Brush? = null,
    rimWidth: Dp = YapNPlayTheme.dimens.hairline,
    /** A faint glow outside the edge (cards); small controls (pills, round buttons) show just their rim. */
    glow: Boolean = true,
): Modifier {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    return this
        // Outside the edge only (an elevation shadow would show through the glass as a box), and faint.
        .then(if (glow) Modifier.edgeGlow(glowColor.copy(alpha = CARD_GLOW_ALPHA), shape, dimens.glowBlur) else Modifier)
        .clip(shape)
        .background(surfaceBrush)
        .border(
            rimWidth,
            // Lit from above like the designs: pale rim on top that
            // turns into the glow color toward the bottom edge.
            rimBrush ?: Brush.verticalGradient(
                colors = listOf(
                    colors.GlassShine.copy(alpha = 0.35f),
                    glowColor.copy(alpha = 0.45f),
                    glowColor.copy(alpha = 0.9f),
                ),
            ),
            shape,
        )
}

/** How strong a card's glow is (share of its glow colour): a hint, not a halo. */
private const val CARD_GLOW_ALPHA = 0.22f
