package com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.board

import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import kotlin.math.min

/** Measures [text] (lines split on "\n") and shrinks it until it fits [maxWidth] × [maxHeight] pixels. */
fun fitText(measurer: TextMeasurer, text: String, style: TextStyle, maxWidth: Float, maxHeight: Float, maxLines: Int = 2): TextLayoutResult {
    val first = measurer.measure(text, style, softWrap = false, maxLines = maxLines)
    val width = first.size.width.toFloat()
    val height = first.size.height.toFloat()
    if (width <= maxWidth && height <= maxHeight) return first
    val factor = min(maxWidth / width, maxHeight / height).coerceAtLeast(MIN_SCALE)
    return measurer.measure(text, style.copy(fontSize = style.fontSize * factor), softWrap = false, maxLines = maxLines)
}

/** Two-word names (Income Tax, Rest House…) go on two lines. */
fun tileLines(name: String): String = name.replace(' ', '\n')

private const val MIN_SCALE = 0.4f
