package com.kglabs28.yapnplay.appengine.ui.components.common

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sin

/**
 * Animated "talking" bars shown next to a member only while they speak.
 */
@Composable
fun VoiceWave(
    modifier: Modifier = Modifier,
    color: Color = YapNPlayTheme.colors.LiveGreen,
) {
    val dimens = YapNPlayTheme.dimens
    val phase by rememberInfiniteTransition().animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(AppConstants.VOICE_WAVE_CYCLE_MS, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
    )

    Canvas(modifier = modifier.height(dimens.voiceWaveHeight)) {
        val bars = AppConstants.VOICE_WAVE_BARS
        val step = size.width / bars
        val stroke = dimens.voiceWaveBar.toPx()
        repeat(bars) { i ->
            val level = 0.25f + 0.75f * abs(sin((i.toFloat() / bars + phase) * 2 * PI.toFloat()))
            val x = step * i + step / 2
            val half = size.height * level / 2
            drawLine(
                color = color,
                start = Offset(x, size.height / 2 - half),
                end = Offset(x, size.height / 2 + half),
                strokeWidth = stroke,
                cap = StrokeCap.Round,
            )
        }
    }
}
