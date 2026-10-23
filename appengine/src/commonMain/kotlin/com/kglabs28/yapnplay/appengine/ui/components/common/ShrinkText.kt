package com.kglabs28.yapnplay.appengine.ui.components.common

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow

/**
 * A label that must fit its space whatever its length (names, table rows,
 * amounts, card lines): the font shrinks, down to [minScale] of [style]'s
 * size, until it fits the width it gets in [maxLines] lines (one by
 * default). Text too long even at the smallest size ends in "…" there, and
 * a tap slides it across in full on one line (another tap stops it);
 * with [slideOnTap] off (a button's label) it never takes the tap, so the
 * button gets it. For running sentences use FitText.
 */
@Composable
fun ShrinkText(
    text: String,
    style: TextStyle,
    modifier: Modifier = Modifier,
    minScale: Float = MIN_SCALE,
    maxLines: Int = 1,
    slideOnTap: Boolean = true,
) = ShrinkText(AnnotatedString(text), style, modifier, minScale, maxLines, slideOnTap)

/** [ShrinkText] for styled text (coloured or bold parts). */
@Composable
fun ShrinkText(
    text: AnnotatedString,
    style: TextStyle,
    modifier: Modifier = Modifier,
    minScale: Float = MIN_SCALE,
    maxLines: Int = 1,
    slideOnTap: Boolean = true,
) {
    // Set once the smallest size still overflows; a new text measures afresh.
    var tooLong by remember(text) { mutableStateOf(false) }
    var sliding by remember(text) { mutableStateOf(false) }
    if (!tooLong) {
        BasicText(
            text = text,
            style = style,
            modifier = modifier,
            maxLines = maxLines,
            softWrap = maxLines > 1,
            // Clip, not Ellipsis: auto-size only shrinks when it sees the text overflow (an ellipsis hides that).
            overflow = TextOverflow.Clip,
            autoSize = TextAutoSize.StepBased(minFontSize = style.fontSize * minScale, maxFontSize = style.fontSize),
            onTextLayout = { if (it.hasVisualOverflow) tooLong = true },
        )
        return
    }
    BasicText(
        text = text,
        style = style.copy(fontSize = style.fontSize * minScale),
        maxLines = if (sliding) 1 else maxLines,
        softWrap = !sliding && maxLines > 1,
        overflow = if (sliding) TextOverflow.Clip else TextOverflow.Ellipsis,
        modifier = if (slideOnTap) {
            modifier
                .clickable(interactionSource = null, indication = null) { sliding = !sliding }
                .then(if (sliding) Modifier.basicMarquee() else Modifier)
        } else {
            modifier
        },
    )
}

private const val MIN_SCALE = 0.6f
