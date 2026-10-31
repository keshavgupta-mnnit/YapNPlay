package com.kglabs28.yapnplay.appengine.ui.components.table

import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.ui.components.common.ShrinkText

/**
 * The MONO table's message under the piles: the stack's total, what just
 * happened (a colour named, a knock-out) or a penalty, one at a time and
 * always the same pill, font and height. [alert] (a stack, a penalty)
 * shows it red; a colour just named ([fill]) in that colour; anything
 * else on the table's dark glass with gold text. A long line shrinks to fit.
 */
@Composable
fun MonoTableMessage(text: String, alert: Boolean, modifier: Modifier = Modifier, fill: Color? = null, word: Pair<String, Color>? = null) {
    val colors = YapNPlayTheme.colors
    val shape = YapNPlayTheme.shapes.pill
    // A colour fill wins over plain red: a stack shows on the colour in play.
    val surface = when {
        fill != null -> Modifier.background(fill)
        alert -> Modifier.background(colors.Error)
        else -> Modifier.arenaGlass(shape)
    }
    // A word in its own colour (the Roulette's colour on the dark glass).
    val shown = buildAnnotatedString {
        append(text)
        word?.let { (w, c) -> text.indexOf(w).takeIf { it >= 0 }?.let { addStyle(SpanStyle(color = c), it, it + w.length) } }
    }
    ShrinkText(
        text = shown,
        style = YapNPlayTheme.typography.label.copy(
            color = if (alert || fill != null) colors.TextPrimary else colors.ArenaCrown,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center,
        ),
        modifier = modifier
            .height(YapNPlayTheme.dimens.monoTableMessageHeight)
            .clip(shape)
            .then(surface)
            .border(YapNPlayTheme.dimens.hairline, colors.TextPrimary.copy(alpha = RIM_ALPHA), shape)
            .padding(horizontal = Spacing.Md)
            .wrapContentHeight(Alignment.CenterVertically),
    )
}

private const val RIM_ALPHA = 0.35f
