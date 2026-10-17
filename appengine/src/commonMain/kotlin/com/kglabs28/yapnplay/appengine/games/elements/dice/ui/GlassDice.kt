package com.kglabs28.yapnplay.appengine.games.elements.dice.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme

/**
 * Milky cream glass die with dark pips (Royal Ludo design). Rolls are
 * driven through [state]; the glow breathes and the tint drifts slowly.
 */
@Composable
fun GlassDice(
    state: GlassDiceState,
    modifier: Modifier = Modifier,
    palette: DicePalette = DicePalette(
        tints = listOf(YapNPlayTheme.colors.DiceGold, YapNPlayTheme.colors.DiceIvory, YapNPlayTheme.colors.DiceAmber),
        deep = YapNPlayTheme.colors.DiceDeep,
        pip = YapNPlayTheme.colors.DicePip,
    ),
) {
    val transition = rememberInfiniteTransition()
    val pulse by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(AppConstants.DICE_PULSE_MS, easing = FastOutSlowInEasing), RepeatMode.Reverse),
    )
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(AppConstants.DICE_TINT_CYCLE_MS, easing = LinearEasing)),
    )
    Canvas(modifier.aspectRatio(1f)) {
        drawGlassDice(state.angles, state.progress, palette, pulse, phase)
    }
}
