package com.kglabs28.yapnplay.appengine.ui.components.table

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
 * shows it red; anything else on glass. A long line shrinks to fit.
 */
@Composable
fun MonoTableMessage(text: String, alert: Boolean, modifier: Modifier = Modifier) {
    val colors = YapNPlayTheme.colors
    val shape = YapNPlayTheme.shapes.pill
    val fill = if (alert) Modifier.background(colors.Error) else Modifier.arenaGlass(shape)
    ShrinkText(
        text = text,
        style = YapNPlayTheme.typography.label.copy(
            color = if (alert) colors.TextPrimary else colors.ArenaCrown,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center,
        ),
        modifier = modifier
            .height(YapNPlayTheme.dimens.monoTableMessageHeight)
            .clip(shape)
            .then(fill)
            .border(YapNPlayTheme.dimens.hairline, colors.TextPrimary.copy(alpha = RIM_ALPHA), shape)
            .padding(horizontal = Spacing.Md)
            .wrapContentHeight(Alignment.CenterVertically),
    )
}

private const val RIM_ALPHA = 0.35f
