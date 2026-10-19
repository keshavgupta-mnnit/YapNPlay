package com.kglabs28.yapnplay.appengine.ui.components.table

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme

/**
 * The MONO table surface the piles sit on: a dark radial felt with a
 * lit centre and a teal rim. The play area is a table, not a
 * transparent hole in the dashboard.
 */
@Composable
fun MonoTableSurface(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val shape = RoundedCornerShape(dimens.panelRadius)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                Brush.radialGradient(
                    colors = listOf(colors.Panel, colors.Background.copy(alpha = FELT_ALPHA)),
                    radius = FELT_RADIUS,
                ),
            )
            .border(dimens.hairline, colors.LudoTeal.copy(alpha = FELT_RIM_ALPHA), shape),
        contentAlignment = Alignment.Center,
        content = content,
    )
}

/** The soft glow under the discard pile, so the centre reads as lit. */
@Composable
fun MonoTableGlow(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val radius = size.minDimension / GLOW_DIVISOR
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color.White.copy(alpha = GLOW_ALPHA), Color.Transparent),
                center = center,
                radius = radius,
            ),
            radius = radius,
        )
    }
}

private const val FELT_ALPHA = 0.85f
private const val FELT_RIM_ALPHA = 0.35f
private const val FELT_RADIUS = 900f
private const val GLOW_ALPHA = 0.06f
private const val GLOW_DIVISOR = 2.2f