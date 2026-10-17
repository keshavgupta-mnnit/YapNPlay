package com.kglabs28.yapnplay.appengine.games.citybaazar.ui.components.board

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import kotlinx.coroutines.launch

/** A token gliding to [target] with a small hop on every change. */
@Composable
fun animateTokenSpot(seat: Int, target: Offset): TokenSpot {
    val position = remember { Animatable(target, Offset.VectorConverter) }
    val lift = remember { Animatable(0f) }
    LaunchedEffect(target) {
        if (position.value == target) return@LaunchedEffect
        launch {
            lift.animateTo(1f, tween(AppConstants.CB_HOP_MS / 2))
            lift.animateTo(0f, tween(AppConstants.CB_HOP_MS / 2))
        }
        position.animateTo(target, tween(AppConstants.CB_HOP_MS))
    }
    return TokenSpot(seat, position.value, lift.value)
}
