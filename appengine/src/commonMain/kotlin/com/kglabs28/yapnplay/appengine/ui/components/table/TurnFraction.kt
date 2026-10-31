package com.kglabs28.yapnplay.appengine.ui.components.table

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import kotlinx.datetime.Clock

/**
 * A seat's turn clock (1 → 0), running down by itself to [endsAtMillis]
 * (this device's clock) over a [totalMs] turn: only the ring redraws, the
 * table is not rebuilt for it. Null [endsAtMillis] holds it full (paused
 * while a move animates, or not this seat's turn).
 */
@Composable
fun rememberTurnFraction(endsAtMillis: Long?, totalMs: Long): State<Float> {
    val shown = remember { Animatable(1f) }
    LaunchedEffect(endsAtMillis, totalMs) {
        if (endsAtMillis == null) return@LaunchedEffect shown.snapTo(1f)
        val now = Clock.System.now().toEpochMilliseconds()
        shown.snapTo(TurnClock.fraction(endsAtMillis, totalMs, now))
        shown.animateTo(0f, tween(TurnClock.left(endsAtMillis, now).toInt(), easing = LinearEasing))
    }
    return shown.asState()
}
