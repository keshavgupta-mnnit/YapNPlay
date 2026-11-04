package com.kglabs28.yapnplay.appengine.games.doodleking.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.ui.components.common.ShrinkText
import com.kglabs28.yapnplay.appengine.ui.components.table.arenaGlass

/**
 * A right-column panel, like City Royale's side cards: the table's dark
 * glass with its [title] centred in bold on a full-width band of [band]
 * colours, then its list.
 */
@Composable
fun DoodleSidePanel(
    title: String,
    band: List<Color>,
    titleColor: Color,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val shape = RoundedCornerShape(YapNPlayTheme.dimens.ludoGlossRadius)
    Column(
        modifier = modifier.arenaGlass(shape).clip(shape),
        verticalArrangement = Arrangement.spacedBy(Spacing.Xxs),
    ) {
        ShrinkText(
            text = title,
            style = YapNPlayTheme.typography.label.copy(color = titleColor, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center),
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.horizontalGradient(band))
                .padding(vertical = Spacing.Xxs),
        )
        Column(
            modifier = Modifier.weight(1f).fillMaxWidth().padding(start = Spacing.Xs, end = Spacing.Xs, bottom = Spacing.Xs),
            verticalArrangement = Arrangement.spacedBy(Spacing.Xxs),
            content = content,
        )
    }
}
