package com.kglabs28.yapnplay.appengine.games.doodleking.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
 * The live leaderboard: everyone by points, centred, the top three on gold,
 * silver and bronze glass. When the order changes the rows slide to their
 * new places and the list goes back to the top; the player cards on the
 * left never move.
 */
@Composable
fun DoodleRankPanel(rows: List<DoodleRankRowUi>, seatTint: (Int) -> Color, modifier: Modifier = Modifier) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val style = YapNPlayTheme.typography.caption
    val listState = rememberLazyListState()
    val medals = listOf(
        Triple(colors.LeagueGoldTop, colors.LeagueGoldBottom, colors.LeagueGoldRim),
        Triple(colors.LeagueSilverTop, colors.LeagueSilverBottom, colors.LeagueSilverRim),
        Triple(colors.LeagueBronzeTop, colors.LeagueBronzeBottom, colors.LeagueBronzeRim),
    )
    // A new order: back to the top, so the leader is always in sight.
    LaunchedEffect(rows.map { it.seat }) { listState.scrollToItem(0) }
    DoodleSidePanel(title = DoodleStrings.dkLeaderboard, modifier = modifier) {
        LazyColumn(state = listState, modifier = Modifier.fillMaxWidth().glassScrollbar(listState), verticalArrangement = Arrangement.spacedBy(Spacing.Xxxs)) {
            itemsIndexed(rows, key = { _, row -> row.seat }) { index, row ->
                val weight = if (row.isMe) FontWeight.Bold else FontWeight.Normal
                val shape = RoundedCornerShape(dimens.cbRowRadius)
                val medal = medals.getOrNull(index)?.let { (top, bottom, rim) ->
                    Modifier.clip(shape).background(Brush.verticalGradient(listOf(top, bottom))).border(dimens.hairline, rim, shape)
                } ?: Modifier
                Row(
                    modifier = Modifier.animateItem().fillMaxWidth().height(dimens.doodleRankRowHeight).then(medal).padding(horizontal = Spacing.Xs),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.Xs),
                ) {
                    BasicText(text = row.place, style = style.copy(color = colors.ArenaCrown, fontWeight = weight, textAlign = TextAlign.Center), modifier = Modifier.width(dimens.iconSmall))
                    ShrinkText(text = row.name, style = style.copy(color = seatTint(row.seat), fontWeight = weight, textAlign = TextAlign.Center), modifier = Modifier.weight(1f))
                    BasicText(text = row.score, style = style.copy(color = colors.TextPrimary, fontWeight = weight, textAlign = TextAlign.Center))
                }
            }
        }
    }
}
