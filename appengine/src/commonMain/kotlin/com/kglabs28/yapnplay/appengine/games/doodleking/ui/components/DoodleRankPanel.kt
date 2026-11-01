package com.kglabs28.yapnplay.appengine.games.doodleking.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.DoodleStrings
import com.kglabs28.yapnplay.appengine.games.doodleking.ui.screens.match.DoodleRankRowUi
import com.kglabs28.yapnplay.appengine.ui.components.common.ShrinkText
import com.kglabs28.yapnplay.appengine.ui.components.common.glassScrollbar

/**
 * The live leaderboard: everyone by points. When the order changes the rows
 * slide to their new places; the player cards on the left never move.
 */
@Composable
fun DoodleRankPanel(rows: List<DoodleRankRowUi>, seatTint: (Int) -> Color, modifier: Modifier = Modifier) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val style = YapNPlayTheme.typography.caption
    val listState = rememberLazyListState()
    DoodleSidePanel(title = DoodleStrings.dkLeaderboard, modifier = modifier) {
        LazyColumn(state = listState, modifier = Modifier.fillMaxWidth().glassScrollbar(listState)) {
            items(rows, key = { it.seat }) { row ->
                val weight = if (row.isMe) FontWeight.Bold else FontWeight.Normal
                Row(
                    modifier = Modifier.animateItem().fillMaxWidth().height(dimens.doodleRankRowHeight),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.Xs),
                ) {
                    BasicText(text = row.place, style = style.copy(color = colors.ArenaCrown, fontWeight = weight), modifier = Modifier.width(dimens.iconSmall))
                    ShrinkText(text = row.name, style = style.copy(color = seatTint(row.seat), fontWeight = weight), modifier = Modifier.weight(1f))
                    BasicText(text = row.score, style = style.copy(color = colors.TextPrimary, fontWeight = weight))
                }
            }
        }
    }
}
