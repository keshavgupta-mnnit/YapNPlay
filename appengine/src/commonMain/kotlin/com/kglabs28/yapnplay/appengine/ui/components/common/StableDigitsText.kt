package com.kglabs28.yapnplay.appengine.ui.components.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer

/**
 * A ticking number ("1:08", "0:45") that never changes width: it takes the
 * room of the same text with every digit as the widest digit, so the pill
 * around it stays still while "1" turns into "8".
 */
@Composable
fun StableDigitsText(text: String, style: TextStyle, modifier: Modifier = Modifier) {
    val measurer = rememberTextMeasurer()
    val widest = remember(style) { DIGITS.maxBy { measurer.measure(it.toString(), style).size.width } }
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        BasicText(text = text.map { if (it.isDigit()) widest else it }.joinToString(""), style = style, maxLines = 1, modifier = Modifier.alpha(0f))
        BasicText(text = text, style = style, maxLines = 1)
    }
}

private const val DIGITS = "0123456789"
