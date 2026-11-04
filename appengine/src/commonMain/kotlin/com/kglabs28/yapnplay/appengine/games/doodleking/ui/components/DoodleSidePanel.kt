package com.kglabs28.yapnplay.appengine.games.doodleking.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.ui.components.common.ShrinkText
import com.kglabs28.yapnplay.appengine.ui.components.table.arenaGlass

/**
 * A right-column panel: the table's dark glass rimmed and glowing in [rim],
 * its [title] centred in a pill that glows the same way at the top, then its list.
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
    val shape = RoundedCornerShape(dimens.ludoGlossRadius)
    val pill = RoundedCornerShape(percent = PILL)
    Column(
        modifier = modifier
            .shadow(dimens.neonGlow, shape, ambientColor = rim, spotColor = rim)
            .arenaGlass(shape)
            .border(dimens.hairline, rim, shape)
            .padding(Spacing.Xs),
        verticalArrangement = Arrangement.spacedBy(Spacing.Xs),
    ) {
        ShrinkText(
            text = title,
            style = YapNPlayTheme.typography.label.copy(color = titleColor, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center),
            modifier = Modifier
                .fillMaxWidth()
                // The header glows too, in the panel's colour.
                .shadow(dimens.neonGlow, pill, ambientColor = rim, spotColor = rim)
                .clip(pill)
                .background(YapNPlayTheme.colors.Background)
                .background(rim.copy(alpha = HEADER_FILL))
                .border(dimens.borderThin, rim, pill)
                .padding(vertical = Spacing.Xxxs, horizontal = Spacing.Sm),
        )
        Column(
            modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = Spacing.Xxs),
            verticalArrangement = Arrangement.spacedBy(Spacing.Xxs),
            content = content,
        )
    }
}

private const val PILL = 50
/** How much of the rim's colour tints the header's dark fill. */
private const val HEADER_FILL = 0.18f
