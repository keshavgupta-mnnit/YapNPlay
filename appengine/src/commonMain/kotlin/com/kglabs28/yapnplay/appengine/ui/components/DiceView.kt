package com.kglabs28.yapnplay.appengine.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme

/**
 * Static dice face showing [value] (1–6). Animation comes later;
 * the caller swaps values to animate.
 */
@Composable
fun DiceView(
    value: Int,
    modifier: Modifier = Modifier,
    faceColor: Color = YapNPlayTheme.colors.TextPrimary,
    pipColor: Color = YapNPlayTheme.colors.Background,
) {
    require(value in 1..6) { "Dice value must be 1-6, was $value" }
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    Box(
        modifier = modifier
            .size(dimens.diceSize)
            .clip(YapNPlayTheme.shapes.button)
            .background(faceColor)
            .border(dimens.borderThin, colors.Cyan.copy(alpha = 0.6f), YapNPlayTheme.shapes.button),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(dimens.diceSize)) {
            val step = size.minDimension / 4f
            val cx = size.width / 2f
            val cy = size.height / 2f
            val pip = step / 4f
            fun pipAt(x: Float, y: Float) {
                drawCircle(color = pipColor, radius = pip, center = Offset(x, y))
            }
            val left = cx - step
            val right = cx + step
            val top = cy - step
            val bottom = cy + step
            when (value) {
                1 -> pipAt(cx, cy)
                2 -> { pipAt(left, top); pipAt(right, bottom) }
                3 -> { pipAt(left, top); pipAt(cx, cy); pipAt(right, bottom) }
                4 -> { pipAt(left, top); pipAt(right, top); pipAt(left, bottom); pipAt(right, bottom) }
                5 -> { pipAt(left, top); pipAt(right, top); pipAt(cx, cy); pipAt(left, bottom); pipAt(right, bottom) }
                6 -> { pipAt(left, top); pipAt(right, top); pipAt(left, cy); pipAt(right, cy); pipAt(left, bottom); pipAt(right, bottom) }
            }
        }
    }
}
