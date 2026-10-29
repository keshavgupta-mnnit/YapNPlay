package com.kglabs28.yapnplay.appengine.core.designsystem

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.Shape

/**
 * [glassPanel] for small controls (top bar pills, round buttons): the
 * same glow and lit border, a more see-through [surface] so the art
 * shows through, plus a sheen across the top half for the shine.
 */
@Composable
fun Modifier.glassPill(
    glowColor: Color,
    surface: Color,
    shape: Shape = YapNPlayTheme.shapes.pill,
    /**
     * The solid colour the pill sits on, when known: the glass is then
     * mixed over it and drawn solid, so the glow's shadow under it can't
     * show through as a box (big keys on a tablet).
     */
    backdrop: Color? = null,
): Modifier {
    val colors = YapNPlayTheme.colors
    val glass = surface.copy(alpha = 0.55f)
    return this
        .glassPanel(
            glowColor = glowColor,
            shape = shape,
            surface = backdrop?.let { glass.compositeOver(it) } ?: glass,
        )
        .background(
            Brush.verticalGradient(
                0f to colors.GlassShine.copy(alpha = 0.18f),
                0.5f to colors.GlassShine.copy(alpha = 0.04f),
                1f to colors.Transparent,
            ),
        )
}
