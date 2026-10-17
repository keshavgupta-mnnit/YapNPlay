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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sin

/**
 * Voice waveform: thin bars across the whole width. While [animated]
 * (the person is talking) it moves like a live audio signal, cyan fading
 * into purple; otherwise it stands still in [colors] (dark grey for a
 * silent or muted member). Bar count follows the width.
 */
@Composable
fun VoiceWave(
    modifier: Modifier = Modifier,
    animated: Boolean = true,
    colors: List<Color> = listOf(YapNPlayTheme.colors.VoiceWaveStart, YapNPlayTheme.colors.VoiceWaveEnd),
) {
    val dimens = YapNPlayTheme.dimens
    val phase = if (animated) {
        val moving by rememberInfiniteTransition().animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(AppConstants.VOICE_WAVE_CYCLE_MS, easing = LinearEasing),
                repeatMode = RepeatMode.Restart,
            ),
        )
        moving
    } else {
        0f
    }

    Canvas(modifier = modifier.height(dimens.voiceWaveHeight)) {
        val spacing = dimens.voiceWaveSpacing.toPx()
        val bars = (size.width / spacing).toInt().coerceAtLeast(1)
        val brush = Brush.horizontalGradient(colors, startX = 0f, endX = size.width)
        val stroke = dimens.voiceWaveBar.toPx()
        val twoPi = 2 * PI.toFloat()
        val mid = size.height / 2
        repeat(bars) { i ->
            val t = i.toFloat() / bars
            // As in the design: bursts of tall bars (slow envelope) with
            // jittery detail inside, joined by runs of tiny dots.
            val burst = abs(sin(t * PI.toFloat() * BURSTS + phase * twoPi)).pow(1.6f)
            val detail = 0.55f + 0.45f * sin(t * 61f + phase * twoPi * 3)
            val level = burst * detail
            val x = spacing * i + spacing / 2
            if (level < DOT_THRESHOLD) {
                drawCircle(brush = brush, radius = stroke * 0.7f, center = Offset(x, mid))
                return@repeat
            }
            val half = size.height * level / 2
            if (animated) {
                // Soft glow behind each bar.
                drawLine(brush, Offset(x, mid - half), Offset(x, mid + half), stroke * 3f, StrokeCap.Round, alpha = 0.25f)
            }
            drawLine(brush, Offset(x, mid - half), Offset(x, mid + half), stroke, StrokeCap.Round)
        }
    }
}

/** Loud bursts across the width, and the level under which a bar is just a dot. */
private const val BURSTS = 3.5f
private const val DOT_THRESHOLD = 0.14f
