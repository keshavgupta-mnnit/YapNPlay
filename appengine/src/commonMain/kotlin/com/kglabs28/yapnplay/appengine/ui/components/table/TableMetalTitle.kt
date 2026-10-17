package com.kglabs28.yapnplay.appengine.ui.components.table

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme

/**
 * The game name in brushed metal (design's "ROYAL LUDO"): a blue glow,
 * a dark engraved outline, and a silver face lit bright at the top and
 * the middle like polished steel.
 */
@Composable
fun TableMetalTitle(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = YapNPlayTheme.typography.startLabel,
) {
    val colors = YapNPlayTheme.colors
    val outline = with(LocalDensity.current) { YapNPlayTheme.dimens.borderThin.toPx() }
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        BasicText(
            text = text,
            maxLines = 1,
            style = style.copy(
                color = colors.LudoMetalOutline,
                drawStyle = Stroke(width = outline),
                shadow = Shadow(color = colors.LudoMetalGlow, blurRadius = style.fontSize.value),
            ),
        )
        BasicText(
            text = text,
            maxLines = 1,
            style = style.copy(
                brush = Brush.verticalGradient(
                    listOf(colors.LudoMetalLight, colors.LudoMetalMid, colors.LudoMetalLight, colors.LudoMetalDark),
                ),
            ),
        )
    }
}
