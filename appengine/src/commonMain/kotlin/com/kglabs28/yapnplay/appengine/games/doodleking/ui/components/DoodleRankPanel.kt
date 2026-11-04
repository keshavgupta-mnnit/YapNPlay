package com.kglabs28.yapnplay.appengine.games.doodleking.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.DoodleStrings
import com.kglabs28.yapnplay.appengine.games.doodleking.ui.screens.match.DoodleRankRowUi
import com.kglabs28.yapnplay.appengine.ui.components.common.ShrinkText
import com.kglabs28.yapnplay.appengine.ui.components.common.glassScrollbar

/**
 * The live leaderboard: everyone by points, each row the panel's whole width
 * (place, then the name from the start, points at the end), on the panel's glass
 * with no pills behind them. When the order changes the rows slide to their
 * new places and the list goes back to the top; the player cards on the
 * left never move.
 */
@Composable
fun DoodleRankPanel(rows: List<DoodleRankRowUi>, seatTint: (Int) -> Color, modifier: Modifier = Modifier) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val style = YapNPlayTheme.typography.caption
    val listState = rememberLazyListState()
    // A new order: back to the top, so the leader is always in sight.
    LaunchedEffect(rows.map { it.seat }) { listState.scrollToItem(0) }
    DoodleSidePanel(
        title = DoodleStrings.dkLeaderboard,
        rim = colors.ArenaCrown,
        titleColor = colors.ArenaCrown,
        modifier = modifier,
    ) {
        // As much room before the rows as the scrollbar keeps after them, so they sit evenly.
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxWidth().padding(start = dimens.scrollbarWidth + Spacing.Xs).glassScrollbar(listState),
            verticalArrangement = Arrangement.spacedBy(Spacing.Xxxs),
        ) {
            items(rows, key = { it.seat }) { row ->
                val weight = if (row.isMe) FontWeight.Bold else FontWeight.Normal
                Row(
                    modifier = Modifier.animateItem().fillMaxWidth().height(dimens.doodleRankRowHeight),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.Xs),
                ) {
                    BasicText(text = row.place, style = style.copy(color = if (row.isMe) seatTint(row.seat) else colors.ArenaCrown, fontWeight = weight, textAlign = TextAlign.Start), modifier = Modifier.width(dimens.iconSmall))
                    ShrinkText(text = row.name, style = style.copy(color = seatTint(row.seat), fontWeight = weight, textAlign = TextAlign.Start), modifier = Modifier.weight(1f))
                    BasicText(text = row.score, style = style.copy(color = colors.TextPrimary, fontWeight = weight, textAlign = TextAlign.End))
                }
            }
        }
    }
}
