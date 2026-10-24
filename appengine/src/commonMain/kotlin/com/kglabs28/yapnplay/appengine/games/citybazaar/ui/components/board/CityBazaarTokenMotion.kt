package com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.board

import kotlinx.coroutines.coroutineScope
import androidx.compose.animation.core.AnimationVector2D
import androidx.compose.animation.core.AnimationVector1D
import com.kglabs28.yapnplay.appengine.core.designsystem.atGameSpeed
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import kotlinx.coroutines.launch

/**
 * A token gliding to [target]. It hops only when it moves to another
 * [tile]; making room for a token arriving on (or leaving) its tile is a
 * small flat shift, so the others never hop along.
 */
@Composable
fun animateTokenSpot(seat: Int, tile: Int, target: Offset): TokenSpot {
    val position = remember { Animatable(target, Offset.VectorConverter) }
    val lift = remember { Animatable(0f) }
    val lastTile = remember { intArrayOf(tile) }
    LaunchedEffect(target) {
        if (position.value == target) return@LaunchedEffect
        val hop = lastTile[0] != tile
        lastTile[0] = tile
        // Full length even with the phone's animations off.
        atGameSpeed { hopTo(position, lift, target, hop) }
    }
    return TokenSpot(seat, position.value, lift.value)
}

/** The token's hop (a lift while it moves) to [target]. */
private suspend fun hopTo(position: Animatable<Offset, AnimationVector2D>, lift: Animatable<Float, AnimationVector1D>, target: Offset, hop: Boolean) = coroutineScope {
    if (hop) {
        launch {
            lift.animateTo(1f, tween(AppConstants.CB_HOP_MS / 2))
            lift.animateTo(0f, tween(AppConstants.CB_HOP_MS / 2))
        }
    }
    position.animateTo(target, tween(AppConstants.CB_HOP_MS))
}
