package com.kglabs28.yapnplay.appengine.games.doodleking.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.DoodleStrings
import com.kglabs28.yapnplay.appengine.games.doodleking.ui.screens.match.DoodleRevealUi
import com.kglabs28.yapnplay.appengine.ui.components.common.ShrinkText
import com.kglabs28.yapnplay.appengine.ui.components.table.arenaGlass

/** Between turns, over the drawing: the word, and what everyone scored this turn (names in their seat colours). */
@Composable
fun DoodleRevealCard(reveal: DoodleRevealUi, seatTint: (Int) -> Color, modifier: Modifier = Modifier) {
    val colors = YapNPlayTheme.colors
    val typography = YapNPlayTheme.typography
    Column(
        modifier = modifier
            .width(YapNPlayTheme.dimens.doodlePickWidth * CARD_SHARE)
            .arenaGlass(RoundedCornerShape(YapNPlayTheme.dimens.ludoGlossRadius))
            .padding(Spacing.Sm),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.Xxs),
    ) {
        BasicText(text = DoodleStrings.dkWordWas, style = typography.caption.copy(color = colors.TextSecondary))
        ShrinkText(text = reveal.word, style = typography.title.copy(color = colors.ArenaCrown))
        reveal.rows.forEach { row ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                // Each name in its seat's colour.
                ShrinkText(text = row.name, style = typography.caption.copy(color = seatTint(row.seat)), modifier = Modifier.weight(1f))
                BasicText(text = row.points, style = typography.caption.copy(color = if (row.gained) colors.ReadyGreen else colors.TextSecondary))
            }
        }
    }
}

/** The reveal card is this share of the word-pick card's width. */
private const val CARD_SHARE = 0.6f
