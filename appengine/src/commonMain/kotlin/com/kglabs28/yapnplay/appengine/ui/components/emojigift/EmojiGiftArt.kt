package com.kglabs28.yapnplay.appengine.ui.components.emojigift

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import io.github.alexzhirkevich.compottie.Compottie
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter

/**
 * One emoji ([emojiId]) drawn [size] square, playing on a loop: in the
 * picker, in flight and over a seat's avatar. Its still glyph shows until
 * the animation is read (and for a number this build has no art for).
 */
@Composable
fun EmojiGiftArt(emojiId: String, size: Dp, modifier: Modifier = Modifier) {
    val composition by rememberLottieComposition(emojiId) {
        LottieCompositionSpec.JsonString(EmojiGiftAnimations.json(emojiId).orEmpty())
    }
    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        if (composition != null) {
            Image(
                painter = rememberLottiePainter(composition = composition, iterations = Compottie.IterateForever),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            val fontSize = with(LocalDensity.current) { (size * GLYPH_SHARE).toSp() }
            BasicText(
                text = EmojiGiftGlyphs.of(emojiId),
                style = TextStyle(fontSize = fontSize, lineHeight = fontSize, textAlign = TextAlign.Center),
                maxLines = 1,
            )
        }
    }
}

/** The still glyph's font size against its box (emoji glyphs draw a little larger than their size). */
private const val GLYPH_SHARE = 0.8f
