package com.kglabs28.yapnplay.appengine.ui.components.emojigift

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme

/**
 * Emojis in flight, over the whole table: each tossed in an arc from the
 * sender's avatar to the receiver's, growing on the way and leaving a dotted
 * trail. Touches pass straight through. A seat that isn't drawn has nothing to fly from or to.
 */
@Composable
fun EmojiGiftFlightLayer(flights: List<EmojiGiftFlightUi>, anchors: EmojiGiftAnchors, modifier: Modifier = Modifier) {
    var origin by remember { mutableStateOf(Offset.Zero) }
    Box(modifier = modifier.fillMaxSize().onGloballyPositioned { origin = it.positionInRoot() }) {
        flights.forEach { flight ->
            val from = anchors.avatar(flight.fromSeat)?.center
            val to = anchors.avatar(flight.toSeat)?.center
            if (from != null && to != null) {
                key(flight.id) { EmojiGiftFlying(flight.emojiId, from - origin, to - origin) }
            }
        }
    }
}

/** One emoji tossed from [from] to [to] (this layer's coordinates). */
@Composable
private fun EmojiGiftFlying(emojiId: String, from: Offset, to: Offset) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val progress = remember { Animatable(0f) }
    LaunchedEffect(Unit) { progress.animateTo(1f, tween(EmojiGiftTiming.FLIGHT_MS, easing = FastOutSlowInEasing)) }
    val half = with(LocalDensity.current) { dimens.emojiGiftFlight.toPx() / 2 }
    val dot = with(LocalDensity.current) { dimens.emojiGiftTrailDot.toPx() }
    // Everything below reads the progress while drawing: one recomposition per flight, not one per frame.
    Canvas(modifier = Modifier.fillMaxSize()) {
        val t = progress.value
        repeat(TRAIL_DOTS) { i ->
            val at = t * i / TRAIL_DOTS
            drawCircle(colors.Gold.copy(alpha = TRAIL_ALPHA * (i + 1) / TRAIL_DOTS), dot, EmojiGiftPath.pointAt(from, to, at))
        }
    }
    EmojiGiftArt(
        emojiId = emojiId,
        size = dimens.emojiGiftFlight,
        modifier = Modifier.graphicsLayer {
            val t = progress.value
            val point = EmojiGiftPath.pointAt(from, to, t)
            translationX = point.x - half
            translationY = point.y - half
            val scale = EmojiGiftPath.scaleAt(t)
            scaleX = scale
            scaleY = scale
        },
    )
}

private const val TRAIL_DOTS = 14
private const val TRAIL_ALPHA = 0.85f
