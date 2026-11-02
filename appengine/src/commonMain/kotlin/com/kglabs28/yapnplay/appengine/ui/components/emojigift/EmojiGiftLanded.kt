package com.kglabs28.yapnplay.appengine.ui.components.emojigift

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp

/** An emoji that just landed on a seat, in place of its avatar: it pops in with a bounce. */
@Composable
fun EmojiGiftLanded(landing: EmojiGiftLandingUi, size: Dp, modifier: Modifier = Modifier) {
    val pop = remember(landing.id) { Animatable(POP_FROM) }
    LaunchedEffect(landing.id) {
        pop.animateTo(1f, spring(dampingRatio = Spring.DampingRatioHighBouncy, stiffness = Spring.StiffnessMediumLow))
    }
    EmojiGiftArt(
        emojiId = landing.emojiId,
        size = size,
        // Read while drawing only: the bounce doesn't recompose the seat.
        modifier = modifier.graphicsLayer {
            scaleX = pop.value
            scaleY = pop.value
        },
    )
}

private const val POP_FROM = 0.3f
