package com.kglabs28.yapnplay.appengine.core.designsystem

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer

/**
 * Fades content in once [visible] turns true, without touching layout:
 * the element keeps its space while hidden, so loaded data never makes
 * neighbours jump. Use for anything filled in after a fetch.
 */
@Composable
fun Modifier.revealWhen(visible: Boolean): Modifier {
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(MotionTokens.STANDARD_MS, easing = MotionTokens.EasingStandard),
    )
    return graphicsLayer { this.alpha = alpha }
}
