package com.kglabs28.yapnplay.appengine.ui.components.table

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme

/** What just happened ("🏆 Riya finishes 1st!", "You took the stack: +6 cards"): gold words on a glass pill on the table. */
@Composable
fun MonoTableNote(text: String, modifier: Modifier = Modifier) {
    val colors = YapNPlayTheme.colors
    BasicText(
        text = text,
        style = YapNPlayTheme.typography.label.copy(color = colors.ArenaCrown, fontWeight = FontWeight.Black, textAlign = TextAlign.Center),
        maxLines = 2,
        modifier = modifier
            .arenaGlass(YapNPlayTheme.shapes.pill)
            .padding(horizontal = Spacing.Md, vertical = Spacing.Xxs),
    )
}
