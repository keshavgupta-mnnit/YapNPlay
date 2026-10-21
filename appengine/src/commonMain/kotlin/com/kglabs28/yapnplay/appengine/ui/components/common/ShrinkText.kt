package com.kglabs28.yapnplay.appengine.ui.components.common

import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow

/**
 * A label that must stay on one line whatever its length (table rows,
 * amounts, card lines): the font shrinks, down to [minScale] of [style]'s
 * size, to fit the width it gets. For running sentences use FitText.
 */
@Composable
fun ShrinkText(
    text: String,
    style: TextStyle,
    modifier: Modifier = Modifier,
    minScale: Float = MIN_SCALE,
) {
    BasicText(
        text = text,
        style = style,
        modifier = modifier,
        maxLines = 1,
        softWrap = false,
        // Clip, not Ellipsis: auto-size only shrinks when it sees the text overflow (an ellipsis hides that).
        overflow = TextOverflow.Clip,
        autoSize = TextAutoSize.StepBased(minFontSize = style.fontSize * minScale, maxFontSize = style.fontSize),
    )
}

private const val MIN_SCALE = 0.6f
