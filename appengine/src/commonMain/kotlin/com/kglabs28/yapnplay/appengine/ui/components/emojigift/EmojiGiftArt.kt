package com.kglabs28.yapnplay.appengine.ui.components.emojigift

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp

/**
 * One emoji ([emojiId]) drawn [size] square: in the picker, in flight and
 * over a seat's avatar. The one place the art is chosen, so the animated
 * versions replace it here.
 */
@Composable
fun EmojiGiftArt(emojiId: String, size: Dp, modifier: Modifier = Modifier) {
    val fontSize = with(LocalDensity.current) { (size * GLYPH_SHARE).toSp() }
    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        BasicText(
            text = EmojiGiftGlyphs.of(emojiId),
            style = TextStyle(fontSize = fontSize, lineHeight = fontSize, textAlign = TextAlign.Center),
            maxLines = 1,
        )
    }
}

/** The glyph's font size against its box (emoji glyphs draw a little larger than their size). */
private const val GLYPH_SHARE = 0.8f
