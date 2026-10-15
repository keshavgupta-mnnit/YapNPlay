package com.kglabs28.yapnplay.appengine.core.designsystem

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing

/**
 * Motion tokens. Durations in milliseconds; one easing for the system.
 */
object MotionTokens {
    const val FAST_MS = 150
    const val STANDARD_MS = 300
    const val SLOW_MS = 500
    val EasingStandard: Easing = FastOutSlowInEasing
    val EasingEnter: Easing = CubicBezierEasing(0.0f, 0.0f, 0.2f, 1.0f)
}
