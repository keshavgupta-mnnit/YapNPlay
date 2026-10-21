package com.kglabs28.yapnplay.appengine.ui.components.table

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme

/** [line] with its highlights: players in [playerTints] (by colour index), money gold, places cyan, card piles pink. */
@Composable
fun feedText(line: TableFeedLineUi, playerTints: List<Color>): AnnotatedString {
    val colors = YapNPlayTheme.colors
    return buildAnnotatedString {
        append(line.text)
        line.spans.forEach { span ->
            val color = when (span.kind) {
                TableFeedSpanKind.PLAYER -> playerTints.getOrNull(span.colour) ?: colors.TextPrimary
                TableFeedSpanKind.MONEY -> colors.Gold
                TableFeedSpanKind.PLACE -> colors.Cyan
                TableFeedSpanKind.CARD -> colors.SocialZoneAccent
            }
            addStyle(SpanStyle(color = color, fontWeight = FontWeight.Bold), span.start, span.end)
        }
    }
}
