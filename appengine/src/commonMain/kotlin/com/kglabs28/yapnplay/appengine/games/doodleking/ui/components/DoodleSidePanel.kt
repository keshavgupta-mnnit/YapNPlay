package com.kglabs28.yapnplay.appengine.games.doodleking.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.ui.components.common.ShrinkText
import com.kglabs28.yapnplay.appengine.ui.components.table.arenaGlass

/**
 * A right-column panel: the table's plain dark glass, its [title] centred at
 * the top inside a glowing outline in [rim] that hugs the text (only the
 * title is coloured; the panel and its list are not), then its list.
 */
@Composable
fun DoodleSidePanel(
    title: String,
    rim: Color,
    titleColor: Color,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val dimens = YapNPlayTheme.dimens
    val pill = RoundedCornerShape(percent = PILL)
    Column(
        modifier = modifier.arenaGlass(RoundedCornerShape(dimens.ludoGlossRadius)).padding(Spacing.Xs),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.Xs),
    ) {
        ShrinkText(
            text = title,
            style = YapNPlayTheme.typography.label.copy(color = titleColor, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center),
            modifier = Modifier
                .pillGlow(rim, Spacing.Xs)
                .border(dimens.borderThin, rim, pill)
                .padding(vertical = Spacing.Xxxs, horizontal = Spacing.Md),
        )
        Column(
            modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = Spacing.Xxs),
            verticalArrangement = Arrangement.spacedBy(Spacing.Xxs),
            content = content,
        )
    }
}

private const val PILL = 50
