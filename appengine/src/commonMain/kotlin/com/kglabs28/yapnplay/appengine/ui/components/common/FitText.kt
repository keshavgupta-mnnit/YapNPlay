package com.kglabs28.yapnplay.appengine.ui.components.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle

/**
 * One line of text that never breaks onto a second row (or mid-word).
 * A short label (up to [FIT_MAX_WORDS] words) shrinks its font, down to
 * [minScale] of [style]'s size, to fit the space; a longer sentence keeps
 * its size and slides when tapped (SlidingText).
 */
@Composable
fun FitText(
    text: String,
    style: TextStyle,
    modifier: Modifier = Modifier,
    minScale: Float = MIN_SCALE,
) {
    if (text.trim().split(WHITESPACE).size > FIT_MAX_WORDS) {
        SlidingText(text = text, style = style, modifier = modifier)
        return
    }
    ShrinkText(text = text, style = style, modifier = modifier, minScale = minScale)
}

private const val MIN_SCALE = 0.6f
private const val FIT_MAX_WORDS = 2
private val WHITESPACE = Regex("\\s+")
