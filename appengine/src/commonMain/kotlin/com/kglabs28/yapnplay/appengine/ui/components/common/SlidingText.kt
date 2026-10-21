package com.kglabs28.yapnplay.appengine.ui.components.common

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle

/**
 * One line that doesn't fit: tap it to slide the rest into view; tap it
 * again, or anywhere else, to stop.
 */
@Composable
fun SlidingText(text: String, style: TextStyle, modifier: Modifier = Modifier) {
    val sliding = LocalSlidingText.current
    val key = remember { Any() }
    BasicText(
        text = text,
        style = style,
        maxLines = 1,
        softWrap = false,
        modifier = modifier
            .then(if (sliding.active === key) Modifier.basicMarquee(iterations = Int.MAX_VALUE, initialDelayMillis = 0) else Modifier)
            .pointerInput(sliding) { detectTapGestures { sliding.tapped(key) } },
    )
}
