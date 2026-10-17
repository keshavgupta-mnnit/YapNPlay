package com.kglabs28.yapnplay.appengine.games.royalludo.ui.components.board

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.games.royalludo.ui.screens.match.PieceMotion
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

/**
 * Where one piece is drawn (canonical grid units) while it moves: a
 * quick arc per square for a [PieceMotion.HOP], a smooth higher glide
 * for a [PieceMotion.SLIDE] (a captured piece going back to base).
 */
@Stable
class PieceMotionState(start: Offset) {
    private val position = Animatable(start, Offset.VectorConverter)
    private val arc = Animatable(0f)
    private var arcHeight = 0f

    val current: Offset get() = position.value

    /** Height above the board, in grid units. */
    val lift: Float get() = sin(PI.toFloat() * arc.value) * arcHeight

    suspend fun moveTo(target: Offset, motion: PieceMotion) = coroutineScope {
        if (target == position.targetValue) return@coroutineScope
        val duration = if (motion == PieceMotion.HOP) AppConstants.LUDO_HOP_MS else AppConstants.LUDO_SLIDE_MS
        arcHeight = if (motion == PieceMotion.HOP) HOP_HEIGHT else SLIDE_HEIGHT
        launch {
            arc.snapTo(0f)
            arc.animateTo(1f, tween(duration, easing = LinearEasing))
        }
        position.animateTo(target, tween(duration, easing = if (motion == PieceMotion.HOP) LinearEasing else FastOutSlowInEasing))
    }

    private companion object {
        const val HOP_HEIGHT = 0.9f
        const val SLIDE_HEIGHT = 1.4f
    }
}

/** A piece's motion that follows [target], animating with [motion] whenever it changes. */
@Composable
fun rememberPieceMotion(target: Offset, motion: PieceMotion): PieceMotionState {
    val state = remember { PieceMotionState(target) }
    LaunchedEffect(target) { state.moveTo(target, motion) }
    return state
}
