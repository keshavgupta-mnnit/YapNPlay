package com.kglabs28.yapnplay.appengine.games.doodleking.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.ui.components.common.ShrinkText
import com.kglabs28.yapnplay.appengine.ui.components.table.arenaGlass

/**
 * A right-column panel, like the profile's Player Stats: the table's plain
 * dark glass, its [title] in a glowing [rim]-coloured pill sitting on the
 * card's top border, in the middle (half above it, the border hidden behind
 * it), then its list. Only the pill is coloured.
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
    val colors = YapNPlayTheme.colors
    val tab = dimens.doodleStatusHeight
    val pill = RoundedCornerShape(percent = PILL)
    Box(modifier = modifier) {
        // The card starts at the pill's middle line; its list starts under the pill.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = tab / 2)
                .arenaGlass(RoundedCornerShape(dimens.ludoGlossRadius))
                .padding(start = Spacing.Xs, end = Spacing.Xs, top = tab / 2 + Spacing.Xxs, bottom = Spacing.Xs),
            verticalArrangement = Arrangement.spacedBy(Spacing.Xxs),
            content = content,
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .height(tab)
                .pillGlow(rim, Spacing.Xs)
                .clip(pill)
                // Solid, so the card's border doesn't show through it.
                .background(colors.Background)
                .background(rim.copy(alpha = PILL_FILL))
                .border(dimens.borderThin, rim, pill)
                .padding(horizontal = Spacing.Md),
            contentAlignment = Alignment.Center,
        ) {
            ShrinkText(
                text = title,
                style = YapNPlayTheme.typography.label.copy(color = titleColor, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center),
            )
        }
    }
}

private const val PILL = 50
/** How much of the rim's colour tints the pill's solid dark fill. */
private const val PILL_FILL = 0.18f
